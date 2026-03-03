;; Chapter 5: Computing with Register Machines
;; 5.1 Designing Register Machines
;; 5.1.4 Using a Stack to Implement Recursion
;; A double recursion
;; Exercise 5.5
;; Exercise 5.6
;; 5.1.5 Instruction Summary
;; 5.2 A Register-Machine Simulator
;; Exercise 5.7
;; 5.2.1 The Machine Model
(define (make-machine register-names ops controller-text)
  (let ((machine (make-new-machine)))
    (for-each (lambda (register-name)
                ((machine 'allocate-register) register-name))
              register-names)
    ((machine 'install-operations) ops)
    ((machine 'install-instruction-sequence)
     (assemble controller-text machine))
    machine))
;; Registers
(define (make-register name)
  (let ((contents '*unassigned*))
    (define (dispatch message)
      (cond ((eq? message 'get) contents)
            ((eq? message 'set)
             (lambda (value) (set! contents value)))
            (else
             (error "Unknown request -- REGISTER" message))))
    dispatch))
(define (get-contents register)
  (register 'get))
(define (set-contents! register value)
  ((register 'set) value))
;; The stack
(define (make-stack)
  (let ((s '()))
    (define (push x)
      (set! s (cons x s)))
    (define (pop)
      (if (null? s)
          (error "Empty stack -- POP")
          (let ((top (car s)))
            (set! s (cdr s))
            top)))
    (define (initialize)
      (set! s '())
      'done)
    (define (dispatch message)
      (cond ((eq? message 'push) push)
            ((eq? message 'pop) (pop))
            ((eq? message 'initialize) (initialize))
            (else (error "Unknown request -- STACK"
                         message))))
    dispatch))
(define (pop stack)
  (stack 'pop))
(define (push stack value)
  ((stack 'push) value))
;; The basic machine
(define (make-new-machine)
  (let ((pc (make-register 'pc))
        (flag (make-register 'flag))
        (stack (make-stack))
        (the-instruction-sequence '()))
    (let ((the-ops
           (list (list 'initialize-stack
                       (lambda () (stack 'initialize)))
                 (list 'print-stack-statistics
                       (lambda () (stack 'print-statistics)))))
          (register-table
           (list (list 'pc pc) (list 'flag flag))))
      (define (allocate-register name)
        (if (assoc name register-table)
            (error "Multiply defined register: " name)
            (set! register-table
                  (cons (list name (make-register name))
                        register-table)))
        'register-allocated)
      (define (lookup-register name)
        (let ((val (assoc name register-table)))
          (if val
              (cadr val)
              (error "Unknown register:" name))))
      (define (execute)
        (let ((insts (get-contents pc)))
          (if (null? insts)
              'done
              (begin
                ((instruction-execution-proc (car insts)))
                (execute)))))
      (define (dispatch message)
        (cond ((eq? message 'start)
               (set-contents! pc the-instruction-sequence)
               (execute))
              ((eq? message 'install-instruction-sequence)
               (lambda (seq) (set! the-instruction-sequence seq)))
              ((eq? message 'allocate-register) allocate-register)
              ((eq? message 'get-register) lookup-register)
              ((eq? message 'install-operations)
               (lambda (ops) (set! the-ops (append the-ops ops))))
              ((eq? message 'stack) stack)
              ((eq? message 'operations) the-ops)
              (else (error "Unknown request -- MACHINE" message))))
      dispatch)))
(define (start machine)
  (machine 'start))
(define (get-register-contents machine register-name)
  (get-contents (get-register machine register-name)))
(define (set-register-contents! machine register-name value)
  (set-contents! (get-register machine register-name) value)
  'done)
(define (get-register machine reg-name)
  ((machine 'get-register) reg-name))
;; The Assembler
(define (assemble controller-text machine)
  (extract-labels
   controller-text
   (lambda (insts labels)
     (update-insts! insts labels machine)
     insts)))
(define (extract-labels text receive)
  (if (null? text)
      (receive '() '())
      (extract-labels
       (cdr text)
       (lambda (insts labels)
         (let ((next-inst (car text)))
           (if (symbol? next-inst)
               (receive insts
                   (cons (make-label-entry next-inst
                                           insts)
                         labels))
               (receive (cons (make-instruction next-inst)
                              insts)
                   labels)))))))
(define (update-insts! insts labels machine)
  (let ((pc (get-register machine 'pc))
        (flag (get-register machine 'flag))
        (stack (machine 'stack))
        (ops (machine 'operations)))
    (for-each
     (lambda (inst)
       (set-instruction-execution-proc!
        inst
        (make-execution-procedure
         (instruction-text inst) labels machine
         pc flag stack ops)))
     insts)))
(define (make-instruction text)
  (cons text '()))
(define (instruction-text inst)
  (car inst))
(define (instruction-execution-proc inst)
  (cdr inst))
(define (set-instruction-execution-proc! inst proc)
  (set-cdr! inst proc))
(define (make-label-entry label-name insts)
  (cons label-name insts))
(define (lookup-label labels label-name)
  (let ((val (assoc label-name labels)))
    (if val
        (cdr val)
        (error "Undefined label -- ASSEMBLE" label-name))))
;; Exercise 5.8
(define (lookup-label labels label-name)
  (let ((val (assoc label-name labels)))
    (if val
        (cdr val)
        #f)))
(define (extract-labels text receive)
  (if (null? text)
      (receive '() '())
      (extract-labels
       (cdr text)
       (lambda (insts labels)
         (let ((next-inst (car text)))
           (if (symbol? next-inst)
               (let ((val (lookup-label labels next-inst)))
                 (if val
                     (error "Duplicate labels -- EXTRACT-LABELS" next-inst)
                     (receive insts
                         (cons (make-label-entry next-inst
                                                 insts)
                               labels))))
               (receive (cons (make-instruction next-inst)
                              insts)
                   labels)))))))
;; 5.2.3 Generating Execution Procedures for Instructions
(define (make-execution-procedure inst labels machine
                                  pc flag stack ops)
  (cond ((eq? (car inst) 'assign)
         (make-assign inst machine labels ops pc))
        ((eq? (car inst) 'test)
         (make-test inst machine labels ops flag pc))
        ((eq? (car inst) 'branch)
         (make-branch inst machine labels flag pc))
        ((eq? (car inst) 'goto)
         (make-goto inst machine labels pc))
        ((eq? (car inst) 'save)
         (make-save inst machine stack pc))
        ((eq? (car inst) 'restore)
         (make-restore inst machine stack pc))
        ((eq? (car inst) 'perform)
         (make-perform inst machine labels ops pc))
        (else (error "Unknown instruction type -- ASSEMBLE"
                     inst))))
;; `Assign' instructions
(define (make-assign inst machine labels operations pc)
  (let ((target
         (get-register machine (assign-reg-name inst)))
        (value-exp (assign-value-exp inst)))
    (let ((value-proc
           (if (operation-exp? value-exp)
               (make-operation-exp
                value-exp machine labels operations)
               (make-primitive-exp
                (car value-exp) machine labels))))
      (lambda ()
        (set-contents! target (value-proc))
        (advance-pc pc)))))
(define (assign-reg-name assign-instruction)
  (cadr assign-instruction))
(define (assign-value-exp assign-instruction)
  (cddr assign-instruction))
(define (advance-pc pc)
  (set-contents! pc (cdr (get-contents pc))))
;; `Test', `branch', and `goto' instructions
(define (make-test inst machine labels operations flag pc)
  (let ((condition (test-condition inst)))
    (if (operation-exp? condition)
        (let ((condition-proc
               (make-operation-exp
                condition machine labels operations)))
          (lambda ()
            (set-contents! flag (condition-proc))
            (advance-pc pc)))
        (error "Bad TEST instruction -- ASSEMBLE" inst))))
(define (test-condition test-instruction)
  (cdr test-instruction))
(define (make-branch inst machine labels flag pc)
  (let ((dest (branch-dest inst)))
    (if (label-exp? dest)
        (let ((insts
               (lookup-label labels (label-exp-label dest))))
          (lambda ()
            (if (get-contents flag)
                (set-contents! pc insts)
                (advance-pc pc))))
        (error "Bad BRANCH instruction -- ASSEMBLE" inst))))
(define (branch-dest branch-instruction)
  (cadr branch-instruction))
(define (make-goto inst machine labels pc)
  (let ((dest (goto-dest inst)))
    (cond ((label-exp? dest)
           (let ((insts
                  (lookup-label labels
                                (label-exp-label dest))))
             (lambda () (set-contents! pc insts))))
          ((register-exp? dest)
           (let ((reg
                  (get-register machine
                                (register-exp-reg dest))))
             (lambda ()
               (set-contents! pc (get-contents reg)))))
          (else (error "Bad GOTO instruction -- ASSEMBLE"
                       inst)))))
(define (goto-dest goto-instruction)
  (cadr goto-instruction))
;; Other instructions
(define (make-save inst machine stack pc)
  (let ((reg (get-register machine
                           (stack-inst-reg-name inst))))
    (lambda ()
      (push stack (get-contents reg))
      (advance-pc pc))))
(define (make-restore inst machine stack pc)
  (let ((reg (get-register machine
                           (stack-inst-reg-name inst))))
    (lambda ()
      (set-contents! reg (pop stack))
      (advance-pc pc))))
(define (stack-inst-reg-name stack-instruction)
  (cadr stack-instruction))
(define (make-perform inst machine labels operations pc)
  (let ((action (perform-action inst)))
    (if (operation-exp? action)
        (let ((action-proc
               (make-operation-exp
                action machine labels operations)))
          (lambda ()
            (action-proc)
            (advance-pc pc)))
        (error "Bad PERFORM instruction -- ASSEMBLE" inst))))
(define (perform-action inst) (cdr inst))
;; Execution procedures for subexpressions
(define (make-primitive-exp exp machine labels)
  (cond ((constant-exp? exp)
         (let ((c (constant-exp-value exp)))
           (lambda () c)))
        ((label-exp? exp)
         (let ((insts
                (lookup-label labels
                              (label-exp-label exp))))
           (lambda () insts)))
        ((register-exp? exp)
         (let ((r (get-register machine
                                (register-exp-reg exp))))
           (lambda () (get-contents r))))
        (else
         (error "Unknown expression type -- ASSEMBLE" exp))))
(define (tagged-list? lst tag)
  (and (pair? lst) (eq? (car lst) tag)))
(define (register-exp? exp) (tagged-list? exp 'reg))
(define (register-exp-reg exp) (cadr exp))
(define (constant-exp? exp) (tagged-list? exp 'const))
(define (constant-exp-value exp) (cadr exp))
(define (label-exp? exp) (tagged-list? exp 'label))
(define (label-exp-label exp) (cadr exp))
(define (make-operation-exp exp machine labels operations)
  (let ((op (lookup-prim (operation-exp-op exp) operations))
        (aprocs
         (map (lambda (e)
                (make-primitive-exp e machine labels))
              (operation-exp-operands exp))))
    (lambda ()
      (apply op (map (lambda (p) (p)) aprocs)))))
(define (operation-exp? exp)
  (and (pair? exp) (tagged-list? (car exp) 'op)))
(define (operation-exp-op operation-exp)
  (cadr (car operation-exp)))
(define (operation-exp-operands operation-exp)
  (cdr operation-exp))
(define (lookup-prim symbol operations)
  (let ((val (assoc symbol operations)))
    (if val
        (cadr val)
        (error "Unknown operation -- ASSEMBLE" symbol))))
;; Exercise 5.9
;; (define (make-operation-exp exp machine labels operations)
;;   (let ((op (lookup-prim (operation-exp-op exp) operations))
;;         (aprocs
;;          (map (lambda (e)
;;                 (if (label-exp? e)
;;                     (error "Operands cannot be labels -- ASSEMBLE" exp)
;;                     (make-primitive-exp e machine labels)))
;;               (operation-exp-operands exp))))
;;     (lambda ()
;;       (apply op (map (lambda (p) (p)) aprocs)))))
;; Exercise 5.10
;; For the sake of the exercise, I change the syntax of two instructions. First
;; is `assign'; second is `test'--both changed to use infix notations.
;; (define (make-execution-procedure inst labels machine
;;                                   pc flag stack ops)
;;   (cond ((eq? (cadr inst) '=)
;;          (make-assign inst machine labels ops pc))
;;         ((eq? (caadr inst) '?)
;;          (make-test inst machine labels ops flag pc))
;;         ((eq? (car inst) 'branch)
;;          (make-branch inst machine labels flag pc))
;;         ((eq? (car inst) 'goto)
;;          (make-goto inst machine labels pc))
;;         ((eq? (car inst) 'save)
;;          (make-save inst machine stack pc))
;;         ((eq? (car inst) 'restore)
;;          (make-restore inst machine stack pc))
;;         ((eq? (car inst) 'perform)
;;          (make-perform inst machine labels ops pc))
;;         (else (error "Unknown instruction type -- ASSEMBLE"
;;                      inst))))
;; (define (make-assign inst machine labels operations pc)
;;   (let ((target
;;          (get-register machine (assign-reg-name inst)))
;;         (value-exp (assign-value-exp inst)))
;;     (let ((value-proc
;;            (if (operation-exp? value-exp)
;;                (make-operation-exp
;;                 value-exp machine labels operations)
;;                (make-primitive-exp
;;                 (car value-exp) machine labels))))
;;       (lambda ()
;;         (set-contents! target (value-proc))
;;         (advance-pc pc)))))
;; (define (assign-reg-name assign-instruction)
;;   (car assign-instruction))
;; (define (assign-value-exp assign-instruction)
;;   (cdddr assign-instruction))
;; (define (make-test inst machine labels operations flag pc)
;;   (let ((condition (test-condition inst)))
;;     (if (operation-exp? condition)
;;         (let ((condition-proc
;;                (make-operation-exp
;;                 condition machine labels operations)))
;;           (lambda ()
;;             (set-contents! flag (condition-proc))
;;             (advance-pc pc)))
;;         (error "Bad TEST instruction -- ASSEMBLE" inst))))
;; (define (test-condition test-instruction)
;;   (let ((op (operation-exp-op (cdr test-instruction)))
;;         (first-operand (car test-instruction))
;;         (second-operatnd (caddr test-instruction)))
;;     (list (list 'op op) first-operand second-operatnd)))
;; Exercise 5.11
;; a.
;; b.
;; (define (make-save inst machine stack pc)
;;   (let ((reg (get-register machine
;;                            (stack-inst-reg-name inst))))
;;     (lambda ()
;;       (push stack (cons reg (get-contents reg)))
;;       (advance-pc pc))))
;; (define (make-restore inst machine stack pc)
;;   (let ((reg (get-register machine
;;                            (stack-inst-reg-name inst))))
;;     (lambda ()
;;       (let* ((popped (pop stack))
;;              (saved-reg (car popped))
;;              (saved-content (cdr popped)))
;;         (if (not (eq? saved-reg reg))
;;             (error "Restore to wrong register -- RESTORE" saved-reg reg)
;;             (begin (set-contents! reg saved-content)
;;                    (advance-pc pc)))))))
;; c.
;; (define (make-new-machine)
;;   (let ((pc (make-register 'pc))
;;         (flag (make-register 'flag))
;;         (stacks '())
;;         (the-instruction-sequence '()))
;;     (let ((the-ops '())
;;           (register-table
;;            (list (list 'pc pc) (list 'flag flag))))
;;       (define (allocate-register name)
;;         (if (assoc name register-table)
;;             (error "Multiply defined register: " name)
;;             (begin
;;               (set! register-table
;;                     (cons (list name (make-register name))
;;                           register-table))
;;               (set! stacks (cons (list name (make-stack)) stacks))))
;;         'register-allocated)
;;       (define (lookup-register name)
;;         (let ((val (assoc name register-table)))
;;           (if val
;;               (cadr val)
;;               (error "Unknown register:" name))))
;;       (define (execute)
;;         (let ((insts (get-contents pc)))
;;           (if (null? insts)
;;               'done
;;               (begin
;;                 ((instruction-execution-proc (car insts)))
;;                 (execute)))))
;;       (define (dispatch message)
;;         (cond ((eq? message 'start)
;;                (set-contents! pc the-instruction-sequence)
;;                (execute))
;;               ((eq? message 'install-instruction-sequence)
;;                (lambda (seq) (set! the-instruction-sequence seq)))
;;               ((eq? message 'allocate-register) allocate-register)
;;               ((eq? message 'get-register) lookup-register)
;;               ((eq? message 'install-operations)
;;                (lambda (ops) (set! the-ops (append the-ops ops))))
;;               ((eq? message 'stacks) stacks)
;;               ((eq? message 'operations) the-ops)
;;               (else (error "Unknown request -- MACHINE" message))))
;;       dispatch)))
;; (define (update-insts! insts labels machine)
;;   (let ((pc (get-register machine 'pc))
;;         (flag (get-register machine 'flag))
;;         (stacks (machine 'stacks))
;;         (ops (machine 'operations)))
;;     (for-each
;;      (lambda (inst)
;;        (set-instruction-execution-proc!
;;         inst
;;         (make-execution-procedure
;;          (instruction-text inst) labels machine
;;          pc flag stacks ops)))
;;      insts)))
;; (define (make-execution-procedure inst labels machine
;;                                   pc flag stacks ops)
;;   (cond ((eq? (car inst) 'assign)
;;          (make-assign inst machine labels ops pc))
;;         ((eq? (car inst) 'test)
;;          (make-test inst machine labels ops flag pc))
;;         ((eq? (car inst) 'branch)
;;          (make-branch inst machine labels flag pc))
;;         ((eq? (car inst) 'goto)
;;          (make-goto inst machine labels pc))
;;         ((eq? (car inst) 'save)
;;          (make-save inst machine stacks pc))
;;         ((eq? (car inst) 'restore)
;;          (make-restore inst machine stacks pc))
;;         ((eq? (car inst) 'perform)
;;          (make-perform inst machine labels ops pc))
;;         (else (error "Unknown instruction type -- ASSEMBLE"
;;                      inst))))
;; (define (make-save inst machine stacks pc)
;;   (let* ((reg-name (stack-inst-reg-name inst))
;;          (reg (get-register machine reg-name)))
;;     (lambda ()
;;       (let ((reg-stack (cadr (assoc reg-name stacks))))
;;         (push reg-stack (get-contents reg))
;;         (advance-pc pc)))))
;; (define (make-restore inst machine stacks pc)
;;   (let* ((reg-name (stack-inst-reg-name inst))
;;          (reg (get-register machine reg-name)))
;;     (lambda ()
;;       (let ((reg-stack (cadr (assoc reg-name stacks))))
;;         (set-contents! reg (pop reg-stack))
;;         (advance-pc pc)))))
;; Test machine
;; (define fib-machine
;;   (make-machine
;;    '(continue n val)
;;    (list (list '< <) (list '- -) (list '+ +))
;;    '(
;;      (assign continue (label fib-done))
;;      fib-loop
;;      (test (op <) (reg n) (const 2))
;;      (branch (label immediate-answer))
;;      (save continue)
;;      (assign continue (label afterfib-n-1))
;;      (save n)
;;      (assign n (op -) (reg n) (const 1))
;;      (goto (label fib-loop))
;;      afterfib-n-1
;;      (restore continue)
;;      (restore n)
;;      (assign n (op -) (reg n) (const 2))
;;      (save continue)
;;      (assign continue (label afterfib-n-2))
;;      (save val)
;;      (goto (label fib-loop))
;;      afterfib-n-2
;;      (assign n (reg val))
;;      (restore continue)
;;      (restore val)
;;      (assign val
;;              (op +) (reg val) (reg n))
;;      (goto (reg continue))
;;      immediate-answer
;;      (assign val (reg n))
;;      (goto (reg continue))
;;      fib-done)))
;; Exercise 5.12
;; (define (make-sorted-set member-lookup-fn compare-fn)
;;   (let ((ss '()))
;;     (define (add e)
;;       (if (not (member-lookup-fn e ss))
;;           (begin
;;             (set! ss (insert e))
;;             #t)
;;           #f))
;;     (define (insert e)
;;       (define (loop before after)
;;         (cond ((null? after)
;;                (append before (list e)))
;;               ((compare-fn e (car after))
;;                (append before (cons e after)))
;;               (else (loop (append before (list (car after)))
;;                           (cdr after)))))
;;       (loop '() ss))
;;     (define (dispatch m)
;;       (cond ((eq? m 'add)
;;              (lambda (e) (add e)))
;;             ((eq? m 'get) ss)))
;;     dispatch))
;; (define (make-new-machine)
;;   (let ((pc (make-register 'pc))
;;         (flag (make-register 'flag))
;;         (stack (make-stack))
;;         (the-instruction-sequence '())
;;         (all-instructions
;;          (make-sorted-set member
;;                           (lambda (i1 i2) (symbol<? (car i1) (car i2)))))
;;         (entry-registers (make-sorted-set memq symbol<?))
;;         (stack-registers (make-sorted-set memq symbol<?))
;;         (register-sources '()))
;;     (let ((the-ops
;;            (list (list 'initialize-stack
;;                        (lambda () (stack 'initialize)))))
;;           (register-table
;;            (list (list 'pc pc) (list 'flag flag))))
;;       (define (allocate-register name)
;;         (if (assoc name register-table)
;;             (error "Multiply defined register: " name)
;;             (begin
;;               (set! register-table
;;                     (cons (list name (make-register name))
;;                           register-table))
;;               (set! register-sources
;;                     (cons
;;                      (list name
;;                            (make-sorted-set
;;                             member
;;                             (lambda (s1 s2) (symbol<? (caar s1) (caar s2)))))
;;                      register-sources))))
;;         'register-allocated)
;;       (define (lookup-register name)
;;         (let ((val (assoc name register-table)))
;;           (if val
;;               (cadr val)
;;               (error "Unknown register:" name))))
;;       (define (execute)
;;         (let ((insts (get-contents pc)))
;;           (if (null? insts)
;;               'done
;;               (begin
;;                 ((instruction-execution-proc (car insts)))
;;                 (execute)))))
;;       (define (dispatch message)
;;         (cond ((eq? message 'start)
;;                (set-contents! pc the-instruction-sequence)
;;                (execute))
;;               ((eq? message 'install-instruction-sequence)
;;                (lambda (seq) (set! the-instruction-sequence seq)))
;;               ((eq? message 'allocate-register) allocate-register)
;;               ((eq? message 'get-register) lookup-register)
;;               ((eq? message 'install-operations)
;;                (lambda (ops) (set! the-ops (append the-ops ops))))
;;               ((eq? message 'stack) stack)
;;               ((eq? message 'operations) the-ops)
;;               ((eq? message 'all-instructions) all-instructions)
;;               ((eq? message 'entry-registers) entry-registers)
;;               ((eq? message 'stack-registers) stack-registers)
;;               ((eq? message 'register-sources) register-sources)
;;               (else (error "Unknown request -- MACHINE" message))))
;;       dispatch)))
;; (define (update-insts! insts labels machine)
;;   (let ((pc (get-register machine 'pc))
;;         (flag (get-register machine 'flag))
;;         (stack (machine 'stack))
;;         (ops (machine 'operations))
;;         (all-instructions (machine 'all-instructions))
;;         (entry-registers (machine 'entry-registers))
;;         (stack-registers (machine 'stack-registers))
;;         (register-sources (machine 'register-sources)))
;;     (for-each
;;      (lambda (inst)
;;        ((all-instructions 'add) (instruction-text inst))
;;        (set-instruction-execution-proc!
;;         inst
;;         (make-execution-procedure
;;          (instruction-text inst) labels machine
;;          pc flag stack ops entry-registers stack-registers register-sources)))
;;      insts)))
;; (define (make-execution-procedure
;;          inst labels machine
;;          pc flag stack ops
;;          entry-registers stack-registers register-sources)
;;   (cond ((eq? (car inst) 'assign)
;;          (make-assign inst machine labels ops pc register-sources))
;;         ((eq? (car inst) 'test)
;;          (make-test inst machine labels ops flag pc))
;;         ((eq? (car inst) 'branch)
;;          (make-branch inst machine labels flag pc))
;;         ((eq? (car inst) 'goto)
;;          (make-goto inst machine labels pc entry-registers))
;;         ((eq? (car inst) 'save)
;;          (make-save inst machine stack pc stack-registers))
;;         ((eq? (car inst) 'restore)
;;          (make-restore inst machine stack pc stack-registers))
;;         ((eq? (car inst) 'perform)
;;          (make-perform inst machine labels ops pc))
;;         (else (error "Unknown instruction type -- ASSEMBLE"
;;                      inst))))
;; (define (make-goto inst machine labels pc entry-registers)
;;   (let ((dest (goto-dest inst)))
;;     (cond ((label-exp? dest)
;;            (let ((insts
;;                   (lookup-label labels
;;                                 (label-exp-label dest))))
;;              (lambda () (set-contents! pc insts))))
;;           ((register-exp? dest)
;;            (let ((reg
;;                   (get-register machine
;;                                 (register-exp-reg dest))))
;;              ((entry-registers 'add) (stack-inst-reg-name dest))
;;              (lambda ()
;;                (set-contents! pc (get-contents reg)))))
;;           (else (error "Bad GOTO instruction -- ASSEMBLE"
;;                        inst)))))
;; (define (make-save inst machine stack pc stack-registers)
;;   (let ((reg (get-register machine
;;                            (stack-inst-reg-name inst))))
;;     ((stack-registers 'add) (stack-inst-reg-name inst))
;;     (lambda ()
;;       (push stack (get-contents reg))
;;       (advance-pc pc))))
;; (define (make-restore inst machine stack pc stack-registers)
;;   (let ((reg (get-register machine
;;                            (stack-inst-reg-name inst))))
;;     ((stack-registers 'add) (stack-inst-reg-name inst))
;;     (lambda ()
;;       (set-contents! reg (pop stack))
;;       (advance-pc pc))))
;; (define (make-assign inst machine labels operations pc register-sources)
;;   (let* ((target-name (assign-reg-name inst))
;;          (target (get-register machine target-name))
;;          (value-exp (assign-value-exp inst)))
;;     (let ((source-set (cadr (assoc target-name register-sources))))
;;       ((source-set 'add) value-exp))
;;     (let ((value-proc
;;            (if (operation-exp? value-exp)
;;                (make-operation-exp
;;                 value-exp machine labels operations)
;;                (make-primitive-exp
;;                 (car value-exp) machine labels))))
;;       (lambda ()
;;         (set-contents! target (value-proc))
;;         (advance-pc pc)))))
;; Exercise 5.13
;; 5.2.4 Monitoring Machine Performance
;; Exercise 5.4
(define recur-expt-machine
  (make-machine
   '(continue n b val)
   (list (list '= =) (list '- -) (list '* *))
   '((assign continue (label expt-done))
     expt-loop
     (save continue)
     (test (op =) (reg n) (const 0))
     (branch (label base-case))
     (assign continue (label after-expt))
     (assign n (op -) (reg n) (const 1))
     (goto (label expt-loop))
     after-expt
     (assign val (op *) (reg b) (reg val))
     (restore continue)
     (goto (reg continue))
     base-case
     (assign val (const 1))
     (restore continue)
     (goto (reg continue))
     expt-done)))
;; b. Iterative exponentiation:
(define iter-expt-machine
  (make-machine
   '(counter product b n)
   (list (list '= =) (list '- -) (list '* *))
   '((assign counter (reg n))
     (assign product (const 1))
     expt-loop
     (test (op =) (reg counter) (const 0))
     (branch (label expt-done))
     (assign counter (op -) (reg counter) (const 1))
     (assign product (op *) (reg product) (reg b))
     (goto (label expt-loop))
     expt-done)))
;; 5.2.4 Monitoring Machine Performance
(define (make-stack)
  (let ((s '())
        (number-pushes 0)
        (max-depth 0)
        (current-depth 0))
    (define (push x)
      (set! s (cons x s))
      (set! number-pushes (+ 1 number-pushes))
      (set! current-depth (+ 1 current-depth))
      (set! max-depth (max current-depth max-depth)))
    (define (pop)
      (if (null? s)
          (error "Empty stack -- POP")
          (let ((top (car s)))
            (set! s (cdr s))
            (set! current-depth (- current-depth 1))
            top)))
    (define (initialize)
      (set! s '())
      (set! number-pushes 0)
      (set! max-depth 0)
      (set! current-depth 0)
      'done)
    (define (print-statistics)
      (newline)
      (display (list 'total-pushes  '= number-pushes
                     'maximum-depth '= max-depth)))
    (define (dispatch message)
      (cond ((eq? message 'push) push)
            ((eq? message 'pop) (pop))
            ((eq? message 'initialize) (initialize))
            ((eq? message 'print-statistics)
             (print-statistics))
            (else
             (error "Unknown request -- STACK" message))))
    dispatch))
;; Exercise 5.14
(define fact-machine
  (make-machine
   '(continue n val)
   (list (list '= =) (list '- -) (list '* *)
         (list 'read
               (lambda ()
                 (newline)
                 (display ";; Input: ")
                 (read)))
         (list 'print
               (lambda (i)
                 (newline)
                 (display ";; Output: ")
                 (display i))))
   '(machine-loop
     (perform (op initialize-stack))
     (assign n (op read))
     (assign continue (label fact-done))
     fact-loop
     (test (op =) (reg n) (const 1))
     (branch (label base-case))
     (save continue)
     (save n)
     (assign n (op -) (reg n) (const 1))
     (assign continue (label after-fact))
     (goto (label fact-loop))
     after-fact
     (restore n)
     (restore continue)
     (assign val (op *) (reg n) (reg val))
     (goto (reg continue))
     base-case
     (assign val (const 1))
     (goto (reg continue))
     fact-done
     (perform (op print) (reg val))
     (perform (op print-stack-statistics))
     (goto (label machine-loop)))))
;; Exercise 5.15, 5.16, 5.17, 5.18, 5.19
(define (make-register name)
  (let ((contents '*unassigned*)
        (trace #f))
    (define (dispatch message)
      (cond ((eq? message 'get) contents)
            ((eq? message 'set)
             (lambda (value)
               (if trace
                   (begin
                     (newline)
                     (display "set ") (display name) (display ": ")
                     (display contents) (display " -> ") (display value)))
               (set! contents value)))
            ((eq? message 'trace-on!)
             (set! trace #t)
             'done)
            ((eq? message 'trace-off!)
             (set! trace #f)
             'done)
            (else
             (error "Unknown request -- REGISTER" message))))
    dispatch))
(define (extract-labels text receive)
  (if (null? text)
      (receive '() '())
      (extract-labels
       (cdr text)
       (lambda (insts labels)
         (let ((next-inst (car text)))
           (if (symbol? next-inst)
               (let ((new-insts
                      (cons (list (list '$start-label next-inst)) insts)))
                 (receive
                     new-insts
                     (cons (make-label-entry next-inst new-insts)
                           labels)))
               (receive
                   (cons (make-instruction next-inst) insts)
                   labels)))))))
(define (control-instruction? inst)
  (string=? "$" (string (string-ref (symbol->string (car inst)) 0))))
(define (is-control-instruction-label? inst)
  (eq? (car inst) '$start-label))
(define (make-new-machine)
  (let ((pc (make-register 'pc))
        (flag (make-register 'flag))
        (stack (make-stack))
        (the-instruction-sequence '())
        (instruction-count 0)
        (trace #f)
        (last-instruction #f)
        (breakpoints '())
        (current-label #f)
        (current-label-step 1))
    (let ((the-ops
           (list (list 'initialize-stack
                       (lambda () (stack 'initialize)))
                 (list 'print-stack-statistics
                       (lambda () (stack 'print-statistics)))))
          (register-table
           (list (list 'pc pc) (list 'flag flag))))
      (define (allocate-register name)
        (if (assoc name register-table)
            (error "Multiply defined register: " name)
            (set! register-table
                  (cons (list name (make-register name))
                        register-table)))
        'register-allocated)
      (define (lookup-register name)
        (let ((val (assoc name register-table)))
          (if val
              (cadr val)
              (error "Unknown register:" name))))
      (define (lookup-breakpoint label) (assoc label breakpoints))
      (define (breakpoint-step breakpoint) (cadr breakpoint))
      (define (execute)
        (let ((insts (get-contents pc)))
          (if (null? insts)
              'done
              (begin
                (if (not (control-instruction? (caar insts)))
                    (set! instruction-count (+ instruction-count 1)))
                (if trace
                    (begin
                      (newline)
                      (if (is-control-instruction-label? (caar insts))
                          (display (cadaar insts))
                          (display (caar insts)))
                      (set! last-instruction (caar insts))))
                (if (is-control-instruction-label? (caar insts))
                    (begin
                      (set! current-label (cadaar insts))
                      (set! current-label-step 1)))
                (if (and current-label (not (null? breakpoints)))
                    (let ((bp (assoc current-label breakpoints)))
                      (if (and bp (= (breakpoint-step bp) current-label-step))
                          (begin
                            (newline)
                            (display "Hitting breakpoint at...")
                            (newline)
                            (display "  label: ")
                            (display current-label)
                            (newline)
                            (display "  instruction: ")
                            (display (caar insts))
                            (newline)
                            (display "Stopping execution...")
                            'done)
                          (execute-instruction insts)))
                    (execute-instruction insts))))))
      (define (execute-instruction insts)
        ((instruction-execution-proc (car insts)))
        (set! current-label-step (+ 1 current-label-step))
        (execute))
      (define (dispatch message)
        (cond ((eq? message 'start)
               (set-contents! pc the-instruction-sequence)
               (execute))
              ((eq? message 'install-instruction-sequence)
               (lambda (seq) (set! the-instruction-sequence seq)))
              ((eq? message 'allocate-register) allocate-register)
              ((eq? message 'get-register) lookup-register)
              ((eq? message 'install-operations)
               (lambda (ops) (set! the-ops (append the-ops ops))))
              ((eq? message 'stack) stack)
              ((eq? message 'operations) the-ops)
              ((eq? message 'instruction-count)
               (let ((last-instruction-count instruction-count))
                 (set! instruction-count 0)
                 last-instruction-count))
              ((eq? message 'trace-on!)
               (set! trace #t))
              ((eq? message 'trace-off!)
               (set! trace #f))
              ((eq? message 'trace) trace)
              ((eq? message 'trace-on-register!)
               (lambda (name)
                 ((lookup-register name) 'trace-on!)))
              ((eq? message 'trace-off-register!)
               (lambda (name)
                 ((lookup-register name) 'trace-off!)))
              ((eq? message 'set-breakpoint)
               (lambda (bp step)
                 (set! breakpoints (cons (list bp step) breakpoints))))
              ((eq? message 'proceed) (execute-instruction (get-contents pc)))
              ((eq? message 'cancel-breakpoint)
               (lambda (bp step)
                 (set! breakpoints (delete (list bp step) breakpoints))
                 'done))
              ((eq? message 'cancel-all-breakpoints)
               (set! breakpoints '())
               'done)
              (else (error "Unknown request -- MACHINE" message))))
      dispatch)))
(define (set-breakpoint machine label n)
  ((machine 'set-breakpoint) label n))
(define (proceed-machine m) (m 'proceed))
(define (cancel-breakpoint m l n) ((m 'cancel-breakpoint) l n))
(define (cancel-all-breakpoints m) (m 'cancel-all-breakpoints))
(define (make-execution-procedure inst labels machine
                                  pc flag stack ops)
  (cond ((eq? (car inst) 'assign)
         (make-assign inst machine labels ops pc))
        ((eq? (car inst) 'test)
         (make-test inst machine labels ops flag pc))
        ((eq? (car inst) 'branch)
         (make-branch inst machine labels flag pc))
        ((eq? (car inst) 'goto)
         (make-goto inst machine labels pc))
        ((eq? (car inst) 'save)
         (make-save inst machine stack pc))
        ((eq? (car inst) 'restore)
         (make-restore inst machine stack pc))
        ((eq? (car inst) 'perform)
         (make-perform inst machine labels ops pc))
        ((control-instruction? inst)
         (lambda () (advance-pc pc)))
        (else (error "Unknown instruction type -- ASSEMBLE"
                     inst))))
;; Debug
(define iter-expt-machine
  (make-machine
   '(counter product b n)
   (list (list '= =) (list '- -) (list '* *))
   '((assign counter (reg n))
     (assign product (const 1))
     expt-loop
     (test (op =) (reg counter) (const 0))
     (branch (label expt-done))
     (assign counter (op -) (reg counter) (const 1))
     (assign product (op *) (reg product) (reg b))
     (goto (label expt-loop))
     expt-done)))
(set-register-contents! iter-expt-machine 'b 3)
(set-register-contents! iter-expt-machine 'n 5)
(start iter-expt-machine)
(iter-expt-machine 'instruction-count)
(iter-expt-machine 'trace-on!)
(start iter-expt-machine)
(iter-expt-machine 'trace-off!)
((iter-expt-machine 'trace-on-register!) 'product)
(start iter-expt-machine)
(define gcd-machine
  (make-machine
   '(a b t)
   (list (list 'rem remainder) (list '= =))
   '(test-b
     (test (op =) (reg b) (const 0))
     (branch (label gcd-done))
     (assign t (op rem) (reg a) (reg b))
     (assign a (reg b))
     (assign b (reg t))
     (goto (label test-b))
     gcd-done)))
(set-register-contents! gcd-machine 'a 56)
(set-register-contents! gcd-machine 'b 24)
(set-breakpoint gcd-machine 'test-b 4)
(start gcd-machine)
(get-register-contents gcd-machine 'a)
(get-register-contents gcd-machine 'b)
(get-register-contents gcd-machine 't)
(proceed-machine gcd-machine)
(get-register-contents gcd-machine 'a)
(get-register-contents gcd-machine 'b)
(get-register-contents gcd-machine 't)
(proceed-machine gcd-machine)
;; 5.3 Storage Allocation and Garbage Collection
;; 5.3.1 Memory as Vectors
;; Representing Lisp data
;; Implementing the primitive list operations
;; (assign <REG_1> (op car) (reg <REG_2>))
;; (assign <REG_1> (op cdr) (reg <REG_2>))
;; (assign <REG_1> (op vector-ref) (reg the-cars) (reg <REG_2>))
;; (assign <REG_1> (op vector-ref) (reg the-cdrs) (reg <REG_2>))
;; (perform (op set-car!) (reg <REG_1>) (reg <REG_2>))
;; (perform (op set-cdr!) (reg <REG_1>) (reg <REG_2>))
;; (perform
;;  (op vector-set!) (reg the-cars) (reg <REG_1>) (reg <REG_2>))
;; (perform
;;  (op vector-set!) (reg the-cdrs) (reg <REG_1>) (reg <REG_2>))
;; (assign <REG_1> (op cons) (reg <REG_2>) (reg <REG_3>))
;; (perform
;;  (op vector-set!) (reg the-cars) (reg free) (reg <REG_2>))
;; (perform
;;  (op vector-set!) (reg the-cdrs) (reg free) (reg <REG_3>))
;; (assign <REG_1> (reg free))
;; (assign free (op +) (reg free) (const 1))
;; Implementing stacks
;; (assign the-stack (op cons) (reg <REG>) (reg the-stack))
;; (assign <REG> (op car) (reg the-stack))
;; (assign the-stack (op cdr) (reg the-stack))
;; (assign the-stack (const ()))
;; Exercise 5.20
;; Exercise 5.21
;; a.
(define count-leaves-machine-1
  (make-machine
   '(tree val continue temp)
   (list (list 'null? null?)
         (list 'not not)
         (list 'car car)
         (list 'cdr cdr)
         (list '+ +)
         (list 'pair? pair?))
   '((assign continue (label done))
     loop
     (test (op null?) (reg tree))
     (branch (label imm-0))
     (assign temp (op pair?) (reg tree))
     (test (op not) (reg temp))
     (branch (label imm-1))
     (save continue)
     (save tree)
     (assign continue (label after-1))
     (assign tree (op car) (reg tree))
     (goto (label loop))
     imm-0
     (assign val (const 0))
     (goto (reg continue))
     imm-1
     (assign val (const 1))
     (goto (reg continue))
     after-1
     (restore tree)
     (save val)
     (assign continue (label after-2))
     (assign tree (op cdr) (reg tree))
     (goto (label loop))
     after-2
     (restore temp)
     (assign val (op +) (reg val) (reg temp))
     (restore continue)
     (goto (reg continue))
     done)))
;; b.
(define count-leaves-machine-2
  (make-machine
   '(tree val continue temp n)
   (list (list 'null? null?)
         (list 'not not)
         (list 'car car)
         (list 'cdr cdr)
         (list '+ +)
         (list 'pair? pair?))
   '((assign continue (label done))
     (assign n (const 0))
     loop
     (test (op null?) (reg tree))
     (branch (label null))
     (assign temp (op pair?) (reg tree))
     (test (op not) (reg temp))
     (branch (label not-pair))
     (save continue)
     (save tree)
     (assign continue (label after-new-n))
     (assign tree (op car) (reg tree))
     (goto (label loop))
     after-new-n
     (restore tree)
     (restore continue)
     (assign n (reg val))
     (assign tree (op cdr) (reg tree))
     (goto (label loop))
     null
     (assign val (reg n))
     (goto (reg continue))
     not-pair
     (assign val (op +) (reg n) (const 1))
     (goto (reg continue))
     done)))
;; Exercise 5.22
(define append-machine
  (make-machine
   '(x y continue val)
   (list (list 'null? null?)
         (list 'cons cons)
         (list 'car car)
         (list 'cdr cdr))
   '((assign continue (label done))
     loop
     (test (op null?) (reg x))
     (branch (label null-x))
     (save continue)
     (save x)
     (assign continue (label after-append))
     (assign x (op cdr) (reg x))
     (goto (label loop))
     null-x
     (assign val (reg y))
     (goto (reg continue))
     after-append
     (restore x)
     (restore continue)
     (assign x (op car) (reg x))
     (assign val (op cons) (reg x) (reg val))
     (goto (reg continue))
     done)))
(define append!-machine
  (make-machine
   '(x y val cdr-x)
   (list (list 'null? null?)
         (list 'cdr cdr)
         (list 'set-cdr! set-cdr!))
   '((save x)
     last-pair
     (assign cdr-x (op cdr) (reg x))
     (test (op null?) (reg cdr-x))
     (branch (label null-cdr-x))
     (assign x (reg cdr-x))
     (goto (label last-pair))
     null-cdr-x
     (assign val (reg x))
     (restore x)
     (assign val (op set-cdr!) (reg val) (reg y))
     (assign val (reg x))
     (goto (label done))
     done)))
;; 5.3.2 Maintaining the Illusion of Infinite Memory
;; Implementation of a stop-and-copy garbage collector
;; begin-garbage-collection
;; (assign free (const 0))
;; (assign scan (const 0))
;; (assign old (reg root))
;; (assign relocate-continue (label reassign-root))
;; (goto (label relocate-old-result-in-new))
;; reassign-root
;; (assign root (reg new))
;; (goto (label gc-loop))
;; gc-loop
;; (test (op =) (reg scan) (reg free))
;; (branch (label gc-flip))
;; (assign old (op vector-ref) (reg new-cars) (reg scan))
;; (assign relocate-continue (label update-car))
;; (goto (label relocate-old-result-in-new))
;; update-car
;; (perform
;;  (op vector-set!) (reg new-cars) (reg scan) (reg new))
;; (assign old (op vector-ref) (reg new-cdrs) (reg scan))
;; (assign relocate-continue (label update-cdr))
;; update-cdr
;; (perform
;;  (op vector-set!) (reg new-cdrs) (reg scan) (reg new))
;; (assign scan (op +) (reg scan) (const 1))
;; (goto (label gc-loop))
;; relocate-old-result-in-new
;; (test (op pointer-to-pair?) (reg old))
;; (branch (label pair))
;; (assign new (reg old))
;; (goto (reg relocate-continue))
;; pair
;; (assign oldcr (op vector-ref) (reg the-cars) (reg old))
;; (test (op broken-heart?) (reg oldcr))
;; (branch (label already-moved))
;; (assign new (reg free)) ; new location for pair
;; ;; Update `free' pointer.
;; (assign free (op +) (reg free) (const 1))
;; ;; Copy the `car' and `cdr' to new memory.
;; (perform (op vector-set!)
;;          (reg new-cars) (reg new) (reg oldcr))
;; (assign oldcr (op vector-ref) (reg the-cdrs) (reg old))
;; (perform (op vector-set!)
;;          (reg new-cdrs) (reg new) (reg oldcr))
;; ;; Construct the broken heart.
;; (perform (op vector-set!)
;;          (reg the-cars) (reg old) (const broken-heart))
;; (perform
;;  (op vector-set!) (reg the-cdrs) (reg old) (reg new))
;; (goto (reg relocate-continue))
;; already-moved
;; (assign new (op vector-ref) (reg the-cdrs) (reg old))
;; (goto (reg relocate-continue))
;; gc-flip
;; (assign temp (reg the-cdrs))
;; (assign the-cdrs (reg new-cdrs))
;; (assign new-cdrs (reg temp))
;; (assign temp (reg the-cars))
;; (assign the-cars (reg new-cars))
;; (assign new-cars (reg temp))
;; 5.4 The Explicit-Control Evaluator
;; Registers and operations
;; 5.4.1 The Core of the Explicit-Control Evaluator
;; Evaluating simple expressions
;; Evaluating procedure applications
;; Procedure application
;; 5.4.2 Sequence Evaluation and Tail Recursion
;; 5.4.3 Conditionals, Assignments, and Definitions
;; Assignments and definitions
;; 5.4.4 Running the Evaluator
;; Monitoring the performance of the evaluator
;; Controller
;; Operations
(define (make-begin seq) (cons 'begin seq))
(define (setup-environment)
  (let ((initial-env
         (extend-environment (primitive-procedure-names)
                             (primitive-procedure-objects)
                             the-empty-environment)))
    (define-variable! 'true true initial-env)
    (define-variable! 'false false initial-env)
    initial-env))
(define (get-global-environment) the-global-environment)
(define (self-evaluating? exp)
  (cond ((number? exp) true)
        ((string? exp) true)
        (else false)))
(define (variable? exp) (symbol? exp))
(define (quoted? exp) (tagged-list? exp 'quote))
(define (assignment? exp) (tagged-list? exp 'set!))
(define (definition? exp) (tagged-list? exp 'define))
(define (lambda? exp) (tagged-list? exp 'lambda))
(define (if? exp) (tagged-list? exp 'if))
(define (begin? exp) (tagged-list? exp 'begin))
(define (application? exp) (pair? exp))
(define (prompt-for-input string)
  (newline) (newline) (display string) (newline))
(define (announce-output string)
  (newline) (display string) (newline))
(define (user-print object)
  (cond ((compound-procedure? object)
         (display (list 'compound-procedure
                        (procedure-parameters object)
                        (procedure-body object)
                        '<procedure-env>)))
        ((compiled-procedure? object)
         (display '<complied-procedure>))
        (else (display object))))
(define (text-of-quotation exp) (cadr exp))
(define (lambda-parameters exp) (cadr exp))
(define (lambda-body exp) (cddr exp))
(define (make-procedure parameters body env)
  (if (null? (definition-expressions body))
      (list 'procedure parameters body env)
      (list 'procedure parameters (list (scan-out-defines body)) env)))
(define (scan-out-defines body)
  (let* ((def-expressions (definition-expressions body))
         (def-variables (definition-variables def-expressions))
         (def-values (definition-values def-expressions)))
    (make-let
     (map (lambda (var) (list var ''*unassigned*)) def-variables)
     (append
      (map (lambda (var val) (list 'set! var val))
           def-variables
           def-values)
      (non-definition-expressions body)))))
(define (definition-expressions body)
  (scan-expressions body (lambda (exp) (tagged-list? exp 'define))))
(define (non-definition-expressions body)
  (scan-expressions body (lambda (exp) (not (tagged-list? exp 'define)))))
(define (scan-expressions body predicate)
  (cond ((null? body) '())
        ((predicate (car body))
         (cons (car body) (scan-expressions (cdr body) predicate)))
        (else (scan-expressions (cdr body) predicate))))
(define (definition-values def-expressions)
  (map definition-value def-expressions))
(define (definition-variables def-expressions)
  (map definition-variable def-expressions))
(define (operator exp) (car exp))
(define (operands exp) (cdr exp))
(define (empty-arglist) '())
(define (no-operands? ops) (null? ops))
(define (first-operand ops) (car ops))
(define (rest-operands ops) (cdr ops))
(define (last-operand? ops) (null? (cdr ops)))
(define (adjoin-arg arg arglist)
  (append arglist (list arg)))
(define (primitive-procedure? proc) (tagged-list? proc 'primitive))
(define primitive-procedures
  (list (list 'car car)
        (list 'cdr cdr)
        (list 'cons cons)
        (list 'null? null?)
        (list '+ +)
        (list '- -)
        (list '* *)
        (list '/ /)
        (list '= =)
        (list 'eq? eq?)
        (list '< <)
        (list '> >)
        (list '= =)
        (list 'display display)
        (list 'square square)
        (list 'not not)
        (list 'list list)
        (list 'even? even?)
        (list 'member member)
        (list 'abs abs)
        (list 'apply apply)
        (list 'cadr cadr)
        (list 'length length)
        (list 'set-car! set-car!)
        (list 'set-cdr! set-cdr!)
        (list 'newline newline)
        (list 'read read)
        (list 'number? number?)
        (list 'pair? pair?)
        (list 'string? string?)
        (list 'symbol? symbol?)
        (list 'error error)
        (list 'cddr cddr)
        (list 'cdadr cdadr)
        (list 'caadr caadr)
        (list 'cadddr cadddr)
        (list 'caddr caddr)
        (list 'cdddr cdddr)
        ))
(define (primitive-procedure-names)
  (map car primitive-procedures))
(define (primitive-implementation proc) (cadr proc))
(define (primitive-procedure-objects)
  (map (lambda (proc) (list 'primitive (cadr proc)))
       primitive-procedures))
(define (apply-primitive-procedure proc args)
  (apply (primitive-implementation proc) args))
(define (compound-procedure? p)
  (and (pair? p) (tagged-list? p 'procedure)))
(define the-empty-environment '())
(define (enclosing-environment env) (cdr env))
(define (first-frame env) (car env))
(define the-empty-environment '())
(define (make-frame variables values)
  (cons variables values))
(define (frame-variables frame) (car frame))
(define (frame-values frame) (cdr frame))
(define (add-binding-to-frame! var val frame)
  (set-car! frame (cons var (car frame)))
  (set-cdr! frame (cons val (cdr frame))))
(define (extend-environment vars vals base-env)
  (if (= (length vars) (length vals))
      (cons (make-frame vars vals) base-env)
      (if (< (length vars) (length vals))
          (error "Too many arguments supplied" vars vals)
          (error "Too few arguments supplied" vars vals))))
(define (lookup-variable-value var env)
  (define (env-loop env)
    (define (scan vars vals)
      (cond ((null? vars)
             (env-loop (enclosing-environment env)))
            ((eq? var (car vars))
             (car vals))
            (else (scan (cdr vars) (cdr vals)))))
    (if (eq? env the-empty-environment)
        (error "Unbound variable" var)
        (let ((frame (first-frame env)))
          (scan (frame-variables frame)
                (frame-values frame)))))
  (env-loop env))
(define (define-variable! var val env)
  (let ((frame (first-frame env)))
    (define (scan vars vals)
      (cond ((null? vars)
             (add-binding-to-frame! var val frame))
            ((eq? var (car vars))
             (set-car! vals val))
            (else (scan (cdr vars) (cdr vals)))))
    (scan (frame-variables frame)
          (frame-values frame))))
(define (procedure-parameters p) (cadr p))
(define (procedure-body p) (caddr p))
(define (procedure-environment p) (cadddr p))
;; Sequence
(define (begin-actions exp) (cdr exp))
(define (first-exp exp) (car exp))
(define (rest-exps exp) (cdr exp))
(define (last-exp? exp) (null? (cdr exp)))
;; Conditionals
(define (if-predicate exp) (cadr exp))
(define (if-consequent exp) (caddr exp))
(define (if-alternative exp)
  (if (not (null? (cdddr exp)))
      (cadddr exp)
      'false))
(define (true? exp) exp)
;; Assignments
(define (assignment-variable exp) (cadr exp))
(define (assignment-value exp) (caddr exp))
(define (set-variable-value! var val env)
  (define (env-loop env)
    (define (scan vars vals)
      (cond ((null? vars)
             (env-loop (enclosing-environment env)))
            ((eq? var (car vars))
             (set-car! vals val))
            (else (scan (cdr vars) (cdr vals)))))
    (if (eq? env the-empty-environment)
        (error "Unbound variable" var)
        (let ((frame (first-frame env)))
          (scan (frame-variables frame)
                (frame-values frame)))))
  (env-loop env))

;; Definitions
(define (definition-variable exp)
  (if (symbol? (cadr exp))
      (cadr exp)
      (caadr exp)))
(define (definition-value exp)
  (if (symbol? (cadr exp))
      (caddr exp)
      (make-lambda (cdadr exp)
                   (cddr exp))))
(define (make-lambda parameters body)
  (append (list 'lambda parameters) body))
;; Cond
(define (make-if predicate consequent alternative)
  (list 'if predicate consequent alternative))
(define (sequence->exp seq)
  (cond ((null? seq) seq)
        ((last-exp? seq) (first-exp seq))
        (else (make-begin seq))))
(define (cond? exp) (tagged-list? exp 'cond))
(define (cond-clauses exp) (cdr exp))
(define (cond-else-clause? clause)
  (eq? (cond-predicate clause) 'else))
(define (cond-predicate clause) (car clause))
(define (cond-actions clause) (cdr clause))
(define (cond->if exp)
  (expand-clauses (cond-clauses exp)))
(define (expand-clauses clauses)
  (if (null? clauses)
      'false                          ; no `else' clause
      (let ((first (car clauses))
            (rest (cdr clauses)))
        (if (cond-else-clause? first)
            (if (null? rest)
                (sequence->exp (cond-actions first))
                (error "ELSE clause isn't last -- COND->IF"
                       clauses))
            (make-if (cond-predicate first)
                     (sequence->exp (cond-actions first))
                     (expand-clauses rest))))))
;; Cond special
(define (first-cond-clauses cond-clauses) (car cond-clauses))
(define (rest-cond-caluses cond-clauses) (cdr cond-clauses))
(define (cond-predicate cond-clause) (car cond-clause))
(define (cond-actions cond-clause) (cdr cond-clause))
(define (is-cond-else-clause? cond-clause) (tagged-list? cond-clause 'else))
;; Let
(define (let? exp) (tagged-list? exp 'let))
(define (let-assignment-clauses exp) (cadr exp))
(define (let-assignment-variables assignments)
  (map car assignments))
(define (let-assignment-values assignments)
  (map cadr assignments))
(define (let-body exp) (cddr exp))
(define (let->combination exp)
  (if (not (named-let? exp))
      (let ((assignment-clauses (let-assignment-clauses exp)))
        (append
         (list (make-lambda (let-assignment-variables assignment-clauses)
                            (let-body exp)))
         (let-assignment-values assignment-clauses)))
      ;; Named let
      (let ((assignment-clauses (named-let-assignment-clauses exp)))
        (sequence->exp
         (list
          (make-procedure-definition
           (named-let-var exp)
           (let-assignment-variables assignment-clauses)
           (named-let-body exp))
          (append (list (named-let-var exp))
                  (let-assignment-values assignment-clauses)))))
      ))
(define (make-let assignment-clauses body)
  (append (list 'let assignment-clauses) body))
;; Named let
(define (named-let? exp)
  (symbol? (cadr exp)))
(define (named-let-var exp) (cadr exp))
(define (named-let-assignment-clauses exp) (caddr exp))
(define (named-let-body exp) (cdddr exp))
;; no tail recursion
(define (no-more-exps? exps) (null? exps))
;; compiled-procedure
(define (compiled-procedure? proc)
  (tagged-list? proc 'compiled-procedure))
(define (make-compiled-procedure entry env)
  (list 'compiled-procedure entry env))
(define (compiled-procedure-entry c-proc) (cadr c-proc))
(define (compiled-procedure-env c-proc) (caddr c-proc))
;; compile-and-run
(define (compile-and-run? exp)
  (tagged-list? exp 'compile-and-run))
(define (assemble-eceval exp)
  (assemble (statements
             (compile (cadadr exp) 'val 'return))
            eceval))
;; ECEVAL
(define eceval-operations
  (list
   (list '* *)
   (list '- -)
   (list 'adjoin-arg adjoin-arg)
   (list 'announce-output announce-output)
   (list 'application? application?)
   (list 'apply apply)
   (list 'apply-primitive-procedure apply-primitive-procedure)
   (list 'assemble-eceval assemble-eceval)
   (list 'assignment-value assignment-value)
   (list 'assignment-variable assignment-variable)
   (list 'assignment? assignment?)
   (list 'begin-actions begin-actions)
   (list 'begin? begin?)
   (list 'compile-and-run? compile-and-run?)
   (list 'compiled-procedure-entry compiled-procedure-entry)
   (list 'compiled-procedure-env compiled-procedure-env)
   (list 'compiled-procedure? compiled-procedure?)
   (list 'compound-procedure? compound-procedure?)
   (list 'cond->if cond->if)
   (list 'cond-actions cond-actions)
   (list 'cond-clauses cond-clauses)
   (list 'cond-predicate cond-predicate)
   (list 'cond? cond?)
   (list 'cons cons)
   (list 'define-variable! define-variable!)
   (list 'definition-value definition-value)
   (list 'definition-variable definition-variable)
   (list 'definition? definition?)
   (list 'empty-arglist empty-arglist)
   (list 'extend-environment extend-environment)
   (list 'false? false?)
   (list 'first-cond-clauses first-cond-clauses)
   (list 'first-exp first-exp)
   (list 'first-operand first-operand)
   (list 'get-global-environment get-global-environment)
   (list 'if-alternative if-alternative)
   (list 'if-consequent if-consequent)
   (list 'if-predicate if-predicate)
   (list 'if? if?)
   (list 'is-cond-else-clause? is-cond-else-clause?)
   (list 'lambda-body lambda-body)
   (list 'lambda-parameters lambda-parameters)
   (list 'lambda? lambda?)
   (list 'last-exp? last-exp?)
   (list 'last-operand? last-operand?)
   (list 'let->combination let->combination)
   (list 'let? let?)
   (list 'list list)
   (list 'lookup-variable-value lookup-variable-value)
   (list 'make-compiled-procedure make-compiled-procedure)
   (list 'make-procedure make-procedure)
   (list 'no-more-exps? no-more-exps?)
   (list 'no-operands? no-operands?)
   (list 'operands operands)
   (list 'operator operator)
   (list 'primitive-procedure? primitive-procedure?)
   (list 'procedure-body procedure-body)
   (list 'procedure-environment procedure-environment)
   (list 'procedure-parameters procedure-parameters)
   (list 'prompt-for-input prompt-for-input)
   (list 'quoted? quoted?)
   (list 'read read)
   (list 'rest-cond-caluses rest-cond-caluses)
   (list 'rest-exps rest-exps)
   (list 'rest-operands rest-operands)
   (list 'self-evaluating? self-evaluating?)
   (list 'set-variable-value! set-variable-value!)
   (list 'text-of-quotation text-of-quotation)
   (list 'true? true?)
   (list 'user-print user-print)
   (list 'variable? variable?)
   ))
(define eceval
  (make-machine
   '(exp env val proc argl continue unev arg1 arg2 compapp)
   eceval-operations
   '(
     (assign compapp (label compound-apply))
     (branch (label external-entry))
     read-eval-print-loop
     (perform (op initialize-stack))
     (perform
      (op prompt-for-input) (const ";;; EC-Eval input:"))
     (assign exp (op read))
     (assign env (op get-global-environment))
     (assign continue (label print-result))
     (goto (label eval-dispatch))
     print-result
     (perform (op print-stack-statistics))
     (perform
      (op announce-output) (const ";;; EC-Eval value:"))
     (perform (op user-print) (reg val))
     (goto (label read-eval-print-loop))
     external-entry
     (perform (op initialize-stack))
     (assign env (op get-global-environment))
     (assign continue (label print-result))
     (goto (reg val))
     unknown-expression-type
     (assign val (const unknown-expression-type-error))
     (goto (label signal-error))
     unknown-procedure-type
     (restore continue)    ; clean up stack (from `apply-dispatch')
     (assign val (const unknown-procedure-type-error))
     (goto (label signal-error))
     signal-error
     (perform (op user-print) (reg val))
     (goto (label read-eval-print-loop))
     eval-dispatch
     (test (op self-evaluating?) (reg exp))
     (branch (label ev-self-eval))
     (test (op variable?) (reg exp))
     (branch (label ev-variable))
     (test (op quoted?) (reg exp))
     (branch (label ev-quoted))
     (test (op assignment?) (reg exp))
     (branch (label ev-assignment))
     (test (op definition?) (reg exp))
     (branch (label ev-definition))
     (test (op if?) (reg exp))
     (branch (label ev-if))
     (test (op lambda?) (reg exp))
     (branch (label ev-lambda))
     (test (op begin?) (reg exp))
     (branch (label ev-begin))
     (test (op cond?) (reg exp))
     (branch (label ev-cond-special))
     (test (op let?) (reg exp))
     (branch (label ev-let))
     (test (op compile-and-run?) (reg exp))
     (branch (label ev-compile-and-run))
     (test (op application?) (reg exp))
     (branch (label ev-application))
     (goto (label unknown-expression-type))
     ev-self-eval
     (assign val (reg exp))
     (goto (reg continue))
     ev-variable
     (assign val (op lookup-variable-value) (reg exp) (reg env))
     (goto (reg continue))
     ev-quoted
     (assign val (op text-of-quotation) (reg exp))
     (goto (reg continue))
     ev-lambda
     (assign unev (op lambda-parameters) (reg exp))
     (assign exp (op lambda-body) (reg exp))
     (assign val (op make-procedure)
             (reg unev) (reg exp) (reg env))
     (goto (reg continue))
     ev-application
     (save continue)
     (save env)
     (assign unev (op operands) (reg exp))
     (save unev)
     (assign exp (op operator) (reg exp))
     (assign continue (label ev-appl-did-operator))
     (goto (label eval-dispatch))
     ev-appl-did-operator
     (restore unev)                  ; the operands
     (restore env)
     (assign argl (op empty-arglist))
     (assign proc (reg val))         ; the operator
     (test (op no-operands?) (reg unev))
     (branch (label apply-dispatch))
     (save proc)
     ev-appl-operand-loop
     (save argl)
     (assign exp (op first-operand) (reg unev))
     (test (op last-operand?) (reg unev))
     (branch (label ev-appl-last-arg))
     (save env)
     (save unev)
     (assign continue (label ev-appl-accumulate-arg))
     (goto (label eval-dispatch))
     ev-appl-accumulate-arg
     (restore unev)
     (restore env)
     (restore argl)
     (assign argl (op adjoin-arg) (reg val) (reg argl))
     (assign unev (op rest-operands) (reg unev))
     (goto (label ev-appl-operand-loop))
     ev-appl-last-arg
     (assign continue (label ev-appl-accum-last-arg))
     (goto (label eval-dispatch))
     ev-appl-accum-last-arg
     (restore argl)
     (assign argl (op adjoin-arg) (reg val) (reg argl))
     (restore proc)
     (goto (label apply-dispatch))
     ev-compile-and-run
     (assign val (op assemble-eceval) (reg exp))
     (assign continue (label print-result))
     (goto (reg val))
     apply-dispatch
     (test (op primitive-procedure?) (reg proc))
     (branch (label primitive-apply))
     (test (op compound-procedure?) (reg proc))
     (branch (label compound-apply))
     (test (op compiled-procedure?) (reg proc))
     (branch (label compiled-apply))
     (goto (label unknown-procedure-type))
     primitive-apply
     (assign val (op apply-primitive-procedure)
             (reg proc)
             (reg argl))
     (restore continue)
     (goto (reg continue))
     compound-apply
     (assign unev (op procedure-parameters) (reg proc))
     (assign env (op procedure-environment) (reg proc))
     (assign env (op extend-environment)
             (reg unev) (reg argl) (reg env))
     (assign unev (op procedure-body) (reg proc))
     (goto (label ev-sequence))
     compiled-apply
     (restore continue)
     (assign val (op compiled-procedure-entry) (reg proc))
     (goto (reg val))
     ev-begin
     (assign unev (op begin-actions) (reg exp))
     (save continue)
     (goto (label ev-sequence))
     ev-sequence
     (assign exp (op first-exp) (reg unev))
     (test (op last-exp?) (reg unev))
     (branch (label ev-sequence-last-exp))
     (save unev)
     (save env)
     (assign continue (label ev-sequence-continue))
     (goto (label eval-dispatch))
     ev-sequence-continue
     (restore env)
     (restore unev)
     (assign unev (op rest-exps) (reg unev))
     (goto (label ev-sequence))
     ev-sequence-last-exp
     (restore continue)
     (goto (label eval-dispatch))
     ;; Conditionals
     ev-if
     (save exp)                    ; save expression for later
     (save env)
     (save continue)
     (assign continue (label ev-if-decide))
     (assign exp (op if-predicate) (reg exp))
     (goto (label eval-dispatch))  ; evaluate the predicate
     ev-if-decide
     (restore continue)
     (restore env)
     (restore exp)
     (test (op true?) (reg val))
     (branch (label ev-if-consequent))
     ev-if-alternative
     (assign exp (op if-alternative) (reg exp))
     (goto (label eval-dispatch))
     ev-if-consequent
     (assign exp (op if-consequent) (reg exp))
     (goto (label eval-dispatch))
     ;; Assignments
     ev-assignment
     (assign unev (op assignment-variable) (reg exp))
     (save unev)                   ; save variable for later
     (assign exp (op assignment-value) (reg exp))
     (save env)
     (save continue)
     (assign continue (label ev-assignment-1))
     (goto (label eval-dispatch))  ; evaluate the assignment value
     ev-assignment-1
     (restore continue)
     (restore env)
     (restore unev)
     (perform
      (op set-variable-value!) (reg unev) (reg val) (reg env))
     (assign val (const ok))
     (goto (reg continue))
     ;; Definitions
     ev-definition
     (assign unev (op definition-variable) (reg exp))
     (save unev)                   ; save variable for later
     (assign exp (op definition-value) (reg exp))
     (save env)
     (save continue)
     (assign continue (label ev-definition-1))
     (goto (label eval-dispatch))  ; evaluate the definition value
     ev-definition-1
     (restore continue)
     (restore env)
     (restore unev)
     (perform
      (op define-variable!) (reg unev) (reg val) (reg env))
     (assign val (const ok))
     (goto (reg continue))
     ev-cond-derived
     (assign exp (op cond->if) (reg exp))
     (goto (label eval-dispatch))
     ev-cond-special
     (assign unev (op cond-clauses) (reg exp))
     ev-cond-loop
     (assign exp (op first-cond-clauses) (reg unev))
     (assign unev (op rest-cond-caluses) (reg unev))
     (test (op is-cond-else-clause?) (reg exp))
     (branch (label ev-cond-actions))
     (save unev)
     (save exp)
     (save env)
     (save continue)
     (assign exp (op cond-predicate) (reg exp))
     (assign continue (label ev-cond-decide))
     (goto (label eval-dispatch))
     ev-cond-decide
     (restore continue)
     (restore env)
     (restore exp)
     (restore unev)
     (test (op true?) (reg val))
     (branch (label ev-cond-actions))
     (goto (label ev-cond-loop))
     ev-cond-actions
     (assign unev (op cond-actions) (reg exp))
     (save continue)
     (goto (label ev-sequence))
     ev-let
     (assign exp (op let->combination) (reg exp))
     (goto (label eval-dispatch))
     )))
;; Tests
;; (define the-global-environment (setup-environment))
;; (start eceval)
;; 1
;; 'x
;; (+ 1 1)
;; (define x 1)
;; x
;; (define inc (lambda (x) (+ x 1)))
;; (inc 1)
;; (define (sq x) (* x x))
;; (sq 2)
;; (define (sum-of-square x y) (+ (sq x) (sq y)))
;; (sum-of-square 2 3)
;; (define (append x y)
;;   (if (null? x)
;;       y
;;       (cons (car x)
;;             (append (cdr x) y))))
;; (append '(a b c) '(d e f))
;; Exercise 5.23
;; (cond (true 1)
;;       (false 2)
;;       (else 3))
;; (cond (false 1)
;;       (true 2)
;;       (else 3))
;; (cond (false 1)
;;       (false 2)
;;       (else 3))
;; Exercise 5.24
;; Exercise 5.25
;; Monitoring the performance of the evaluator
;; Exercise 5.29
;; 5.5 Compilation
;; 5.5 Compilation
;; An overview of the compiler
;; 5.5.1 Structure of the Compiler
(define (compile exp target linkage)
  (cond ((self-evaluating? exp)
         (compile-self-evaluating exp target linkage))
        ((quoted? exp) (compile-quoted exp target linkage))
        ((variable? exp)
         (compile-variable exp target linkage))
        ((assignment? exp)
         (compile-assignment exp target linkage))
        ((definition? exp)
         (compile-definition exp target linkage))
        ((if? exp) (compile-if exp target linkage))
        ((lambda? exp) (compile-lambda exp target linkage))
        ((begin? exp)
         (compile-sequence (begin-actions exp)
                           target
                           linkage))
        ((cond? exp) (compile (cond->if exp) target linkage))
        ((application? exp)
         (compile-application exp target linkage))
        (else
         (error "Unknown expression type -- COMPILE" exp))))
;; Targets and linkages
;; Instruction sequences and stack usage
(define (make-instruction-sequence needs modifies statements)
  (list needs modifies statements))
(define (empty-instruction-sequence)
  (make-instruction-sequence '() '() '()))
;; Exercise 5.31
;; Exercise 5.32
;; 5.5.2 Compiling Expressions
;; Compiling linkage code
(define (compile-linkage linkage)
  (cond ((eq? linkage 'return)
         (make-instruction-sequence '(continue) '()
                                    '((goto (reg continue)))))
        ((eq? linkage 'next)
         (empty-instruction-sequence))
        (else
         (make-instruction-sequence '() '()
                                    `((goto (label ,linkage)))))))
(define (end-with-linkage linkage instruction-sequence)
  (preserving '(continue)
              instruction-sequence
              (compile-linkage linkage)))
;; Compiling simple expressions
(define (compile-self-evaluating exp target linkage)
  (end-with-linkage
   linkage
   (make-instruction-sequence
    '() (list target)
    `((assign ,target (const ,exp))))))
(define (compile-quoted exp target linkage)
  (end-with-linkage
   linkage
   (make-instruction-sequence
    '() (list target)
    `((assign ,target (const ,(text-of-quotation exp)))))))
(define (compile-variable exp target linkage)
  (end-with-linkage
   linkage
   (make-instruction-sequence
    '(env) (list target)
    `((assign ,target
              (op lookup-variable-value)
              (const ,exp)
              (reg env))))))
(define (compile-assignment exp target linkage)
  (let ((var (assignment-variable exp))
        (get-value-code
         (compile (assignment-value exp) 'val 'next)))
    (end-with-linkage linkage
                      (preserving '(env)
                                  get-value-code
                                  (make-instruction-sequence '(env val) (list target)
                                                             `((perform (op set-variable-value!)
                                                                        (const ,var)
                                                                        (reg val)
                                                                        (reg env))
                                                               (assign ,target (const ok))))))))
(define (compile-definition exp target linkage)
  (let ((var (definition-variable exp))
        (get-value-code
         (compile (definition-value exp) 'val 'next)))
    (end-with-linkage linkage
                      (preserving '(env)
                                  get-value-code
                                  (make-instruction-sequence '(env val) (list target)
                                                             `((perform (op define-variable!)
                                                                        (const ,var)
                                                                        (reg val)
                                                                        (reg env))
                                                               (assign ,target (const ok))))))))
;; Compiling conditional expressions
;; make-label
(define label-counter 0)

(define (new-label-number)
  (set! label-counter (+ 1 label-counter))
  label-counter)

(define (make-label name)
  (string->symbol
   (string-append (symbol->string name)
                  (number->string (new-label-number)))))
(define (compile-if exp target linkage)
  (let ((t-branch (make-label 'true-branch))
        (f-branch (make-label 'false-branch))
        (after-if (make-label 'after-if)))
    (let ((consequent-linkage
           (if (eq? linkage 'next) after-if linkage)))
      (let ((p-code (compile (if-predicate exp) 'val 'next))
            (c-code
             (compile
              (if-consequent exp) target consequent-linkage))
            (a-code
             (compile (if-alternative exp) target linkage)))
        (preserving '(env continue)
                    p-code
                    (append-instruction-sequences
                     (make-instruction-sequence '(val) '()
                                                `((test (op false?) (reg val))
                                                  (branch (label ,f-branch))))
                     (parallel-instruction-sequences
                      (append-instruction-sequences t-branch c-code)
                      (append-instruction-sequences f-branch a-code))
                     after-if))))))
;; Compiling sequences
(define (compile-sequence seq target linkage)
  (if (last-exp? seq)
      (compile (first-exp seq) target linkage)
      (preserving '(env continue)
                  (compile (first-exp seq) target 'next)
                  (compile-sequence (rest-exps seq) target linkage))))
;; Compiling `lambda' expressions
(define (make-compiled-procedure entry env)
  (list 'compiled-procedure entry env))

(define (compiled-procedure? proc)
  (tagged-list? proc 'compiled-procedure))

(define (compiled-procedure-entry c-proc) (cadr c-proc))

(define (compiled-procedure-env c-proc) (caddr c-proc))

(define (compile-lambda exp target linkage)
  (let ((proc-entry (make-label 'entry))
        (after-lambda (make-label 'after-lambda)))
    (let ((lambda-linkage
           (if (eq? linkage 'next) after-lambda linkage)))
      (append-instruction-sequences
       (tack-on-instruction-sequence
        (end-with-linkage lambda-linkage
                          (make-instruction-sequence '(env) (list target)
                                                     `((assign ,target
                                                               (op make-compiled-procedure)
                                                               (label ,proc-entry)
                                                               (reg env)))))
        (compile-lambda-body exp proc-entry))
       after-lambda))))
(define (compile-lambda-body exp proc-entry)
  (let ((formals (lambda-parameters exp)))
    (append-instruction-sequences
     (make-instruction-sequence '(env proc argl) '(env)
                                `(,proc-entry
                                  (assign env (op compiled-procedure-env) (reg proc))
                                  (assign env
                                          (op extend-environment)
                                          (const ,formals)
                                          (reg argl)
                                          (reg env))))
     (compile-sequence (lambda-body exp) 'val 'return))))
;; 5.5.3 Compiling Combinations
(define (compile-application exp target linkage)
  (let ((proc-code (compile (operator exp) 'proc 'next))
        (operand-codes
         (map (lambda (operand) (compile operand 'val 'next))
              (operands exp))))
    (preserving '(env continue)
                proc-code
                (preserving '(proc continue)
                            (construct-arglist operand-codes)
                            (compile-procedure-call target linkage)))))
(define (construct-arglist operand-codes)
  (let ((operand-codes (reverse operand-codes)))
    (if (null? operand-codes)
        (make-instruction-sequence '() '(argl)
                                   '((assign argl (const ()))))
        (let ((code-to-get-last-arg
               (append-instruction-sequences
                (car operand-codes)
                (make-instruction-sequence '(val) '(argl)
                                           '((assign argl (op list) (reg val)))))))
          (if (null? (cdr operand-codes))
              code-to-get-last-arg
              (preserving '(env)
                          code-to-get-last-arg
                          (code-to-get-rest-args
                           (cdr operand-codes))))))))

(define (code-to-get-rest-args operand-codes)
  (let ((code-for-next-arg
         (preserving '(argl)
                     (car operand-codes)
                     (make-instruction-sequence '(val argl) '(argl)
                                                '((assign argl
                                                          (op cons) (reg val) (reg argl)))))))
    (if (null? (cdr operand-codes))
        code-for-next-arg
        (preserving '(env)
                    code-for-next-arg
                    (code-to-get-rest-args (cdr operand-codes))))))
;; Applying procedures
(define (compile-procedure-call target linkage)
  (let ((primitive-branch (make-label 'primitive-branch))
        (compiled-branch (make-label 'compiled-branch))
        (after-call (make-label 'after-call)))
    (let ((compiled-linkage
           (if (eq? linkage 'next) after-call linkage)))
      (append-instruction-sequences
       (make-instruction-sequence '(proc) '()
                                  `((test (op primitive-procedure?) (reg proc))
                                    (branch (label ,primitive-branch))))
       (parallel-instruction-sequences
        (append-instruction-sequences
         compiled-branch
         (compile-proc-appl target compiled-linkage))
        (append-instruction-sequences
         primitive-branch
         (end-with-linkage linkage
                           (make-instruction-sequence '(proc argl)
                                                      (list target)
                                                      `((assign ,target
                                                                (op apply-primitive-procedure)
                                                                (reg proc)
                                                                (reg argl)))))))
       after-call))))
;; Applying compiled procedures
(define all-regs '(env proc val argl continue))
(define (compile-proc-appl target linkage)
  (cond ((and (eq? target 'val) (not (eq? linkage 'return)))
         (make-instruction-sequence '(proc) all-regs
                                    `((assign continue (label ,linkage))
                                      (assign val (op compiled-procedure-entry)
                                              (reg proc))
                                      (goto (reg val)))))
        ((and (not (eq? target 'val))
              (not (eq? linkage 'return)))
         (let ((proc-return (make-label 'proc-return)))
           (make-instruction-sequence '(proc) all-regs
                                      `((assign continue (label ,proc-return))
                                        (assign val (op compiled-procedure-entry)
                                                (reg proc))
                                        (goto (reg val))
                                        ,proc-return
                                        (assign ,target (reg val))
                                        (goto (label ,linkage))))))
        ((and (eq? target 'val) (eq? linkage 'return))
         (make-instruction-sequence '(proc continue) all-regs
                                    '((assign val (op compiled-procedure-entry)
                                              (reg proc))
                                      (goto (reg val)))))
        ((and (not (eq? target 'val)) (eq? linkage 'return))
         (error "return linkage, target not val -- COMPILE"
                target))))
;; 5.5.4 Combining Instruction Sequences
(define (registers-needed s)
  (if (symbol? s) '() (car s)))

(define (registers-modified s)
  (if (symbol? s) '() (cadr s)))

(define (statements s)
  (if (symbol? s) (list s) (caddr s)))
(define (needs-register? seq reg)
  (memq reg (registers-needed seq)))

(define (modifies-register? seq reg)
  (memq reg (registers-modified seq)))
(define (append-instruction-sequences . seqs)
  (define (append-2-sequences seq1 seq2)
    (make-instruction-sequence
     (list-union (registers-needed seq1)
                 (list-difference (registers-needed seq2)
                                  (registers-modified seq1)))
     (list-union (registers-modified seq1)
                 (registers-modified seq2))
     (append (statements seq1) (statements seq2))))
  (define (append-seq-list seqs)
    (if (null? seqs)
        (empty-instruction-sequence)
        (append-2-sequences (car seqs)
                            (append-seq-list (cdr seqs)))))
  (append-seq-list seqs))
(define (list-union s1 s2)
  (cond ((null? s1) s2)
        ((memq (car s1) s2) (list-union (cdr s1) s2))
        (else (cons (car s1) (list-union (cdr s1) s2)))))
(define (list-difference s1 s2)
  (cond ((null? s1) '())
        ((memq (car s1) s2) (list-difference (cdr s1) s2))
        (else (cons (car s1)
                    (list-difference (cdr s1) s2)))))
(define (preserving regs seq1 seq2)
  (if (null? regs)
      (append-instruction-sequences seq1 seq2)
      (let ((first-reg (car regs)))
        (if (and (needs-register? seq2 first-reg)
                 (modifies-register? seq1 first-reg))
            (preserving (cdr regs)
                        (make-instruction-sequence
                         (list-union (list first-reg)
                                     (registers-needed seq1))
                         (list-difference (registers-modified seq1)
                                          (list first-reg))
                         (append `((save ,first-reg))
                                 (statements seq1)
                                 `((restore ,first-reg))))
                        seq2)
            (preserving (cdr regs) seq1 seq2)))))
(define (tack-on-instruction-sequence seq body-seq)
  (make-instruction-sequence
   (registers-needed seq)
   (registers-modified seq)
   (append (statements seq) (statements body-seq))))
(define (parallel-instruction-sequences seq1 seq2)
  (make-instruction-sequence
   (list-union (registers-needed seq1)
               (registers-needed seq2))
   (list-union (registers-modified seq1)
               (registers-modified seq2))
   (append (statements seq1) (statements seq2))))
;; Exercise 5.37
;; (define (preserving regs seq1 seq2)
;;   (if (null? regs)
;;       (append-instruction-sequences seq1 seq2)
;;       (let ((first-reg (car regs)))
;;         (if #t
;;             (preserving (cdr regs)
;;                         (make-instruction-sequence
;;                          (list-union (list first-reg)
;;                                      (registers-needed seq1))
;;                          (list-difference (registers-modified seq1)
;;                                           (list first-reg))
;;                          (append `((save ,first-reg))
;;                                  (statements seq1)
;;                                  `((restore ,first-reg))))
;;                         seq2)
;;             (preserving (cdr regs) seq1 seq2)))))
;; Exercise 5.38
(define (spread-arguments operand-list)
  (let* ((first-operand (car operand-list))
         (second-operand (cadr operand-list))
         (first-operand-code (compile first-operand 'arg1 'next))
         (second-operand-code (compile second-operand 'arg2 'next)))
    (preserving '(arg1) first-operand-code second-operand-code)))
(define (compile exp target linkage)
  (cond ((self-evaluating? exp)
         (compile-self-evaluating exp target linkage))
        ((quoted? exp) (compile-quoted exp target linkage))
        ((variable? exp)
         (compile-variable exp target linkage))
        ((assignment? exp)
         (compile-assignment exp target linkage))
        ((definition? exp)
         (compile-definition exp target linkage))
        ((if? exp) (compile-if exp target linkage))
        ((lambda? exp) (compile-lambda exp target linkage))
        ((begin? exp)
         (compile-sequence (begin-actions exp)
                           target
                           linkage))
        ((cond? exp) (compile (cond->if exp) target linkage))
        ((let? exp) (compile (let->combination exp) target linkage))
        ;; primitive operations
        ((primitive--? exp) (compile-primitive-- exp target linkage))
        ((primitive-*? exp) (compile-primitive-* exp target linkage))
        ((application? exp)
         (compile-application exp target linkage))
        (else
         (error "Unknown expression type -- COMPILE" exp))))
(define (primitive--? exp) (and (pair? exp) (tagged-list? exp '-)))
(define (primitive-*? exp) (and (pair? exp) (tagged-list? exp '*)))
(define (compile-primitive-- exp target linkage)
  (compile-primitive-binary '- exp target linkage))
(define (compile-primitive-* exp target linkage)
  (compile-primitive-binary '* exp target linkage))
(define (compile-primitive-binary op exp target linkage)
  (end-with-linkage
   linkage
   (append-instruction-sequences
    (spread-arguments op (cdr exp))
    (make-instruction-sequence
     '(arg1 arg2) (list target)
     `((assign ,target (op ,op) (reg arg1) (reg arg2)))))))
(define (spread-arguments op operands)
  (define (loop operands instructions)
    (let ((arg2-code (compile (car operands) 'arg2 'next)))
      (if (null? (cdr operands))
          (preserving '(arg1 env) instructions arg2-code)
          (loop (cdr operands)
                (append-instruction-sequences
                 (preserving '(arg1 env) instructions arg2-code)
                 (make-instruction-sequence
                  '(arg1 arg2) '(arg1)
                  `((assign arg1 (op ,op) (reg arg1) (reg arg2)))))))))
  (let ((arg1-code (compile (car operands) 'arg1 'next)))
    (loop (cdr operands) arg1-code)))
;; 5.5.6 Lexical Addressing
;; (define (frame-idx address) (car address))
;; (define (var-idx address) (cadr address))
;; (define (lexical-address-lookup address runtime-envs)
;;   (let* ((bindings (list-ref (list-ref runtime-envs
;;                                        (frame-idx address))
;;                              (var-idx address)))
;;          (var (car bindings))
;;          (val (cadr bindings)))
;;     (if (eq? val '*unassigned*)
;;         (error "Unbound variable" var)
;;         val)))
;; (define (compile exp target linkage ctenv)
;;   (cond ((self-evaluating? exp)
;;          (compile-self-evaluating exp target linkage))
;;         ((quoted? exp) (compile-quoted exp target linkage))
;;         ((variable? exp)
;;          (compile-variable exp target linkage ctenv))
;;         ((assignment? exp)
;;          (compile-assignment exp target linkage ctenv))
;;         ((definition? exp)
;;          (compile-definition exp target linkage ctenv))
;;         ((if? exp) (compile-if exp target linkage))
;;         ((lambda? exp) (compile-lambda exp target linkage ctenv))
;;         ((begin? exp)
;;          (compile-sequence (begin-actions exp)
;;                            target
;;                            linkage))
;;         ((cond? exp) (compile (cond->if exp) target linkage) ctenv)
;;         ((let? exp) (compile (let->combination exp) target linkage ctenv))
;;         ((primitive--? exp) (compile-primitive-- exp target linkage ctenv))
;;         ((primitive-*? exp) (compile-primitive- *exp target linkage ctenv))
;;         ((application? exp)
;;          (compile-application exp target linkage ctenv))
;;         (else
;;          (error "Unknown expression type -- COMPILE" exp))))
;; (define (compile-lambda exp target linkage ctenv)
;;   (let ((proc-entry (make-label 'entry))
;;         (after-lambda (make-label 'after-lambda)))
;;     (let ((lambda-linkage
;;            (if (eq? linkage 'next) after-lambda linkage)))
;;       (append-instruction-sequences
;;        (tack-on-instruction-sequence
;;         (end-with-linkage
;;          lambda-linkage
;;          (make-instruction-sequence '(env) (list target)
;;                                     `((assign ,target
;;                                               (op make-compiled-procedure)
;;                                               (label ,proc-entry)
;;                                               (reg env)))))
;;         (compile-lambda-body exp proc-entry ctenv))
;;        after-lambda))))
;; (define (compile-lambda-body exp proc-entry ctenv)
;;   (let* ((formals (lambda-parameters exp))
;;          (body (lambda-body exp))
;;          (seq (if (null? (definition-expressions body))
;;                   body
;;                   (scan-out-defines body)))
;;          (ctenv (cons formals ctenv)))
;;     (append-instruction-sequences
;;      (make-instruction-sequence
;;       '(env proc argl) '(env)
;;       `(,proc-entry
;;         (assign env (op compiled-procedure-env) (reg proc))
;;         (assign env
;;                 (op extend-environment)
;;                 (const ,formals)
;;                 (reg argl)
;;                 (reg env))))
;;      (compile-sequence seq 'val 'return ctenv))))
;; (define (compile-sequence seq target linkage ctenv)
;;   (if (last-exp? seq)
;;       (compile (first-exp seq) target linkage ctenv)
;;       (preserving '(env continue)
;;                   (compile (first-exp seq) target 'next ctenv)
;;                   (compile-sequence (rest-exps seq) target linkage ctenv))))
;; (define (compile-variable exp target linkage ctenv)
;;   (let* ((lexical-address (get-lexical-address exp ctenv))
;;          (lookup-key (if (null? lexical-address) exp lexical-address))
;;          (lookup-proc (if (null? lexical-address)
;;                           'lookup-variable-value
;;                           'lexical-address-lookup)))
;;     (end-with-linkage
;;      linkage
;;      (make-instruction-sequence
;;       '(env) (list target)
;;       `((assign ,target
;;                 (op ,lookup-proc)
;;                 (const ,lookup-key)
;;                 (reg env)))))))
;; (define (get-lexical-address var ctenv)
;;   (define (loop frame-idx var-idx ctenv)
;;     (cond ((null? ctenv) '())
;;           ((null? (car ctenv)) (loop (+ frame-idx 1) 0 (cdr ctenv)))
;;           ((eq? var (caar ctenv)) (list frame-idx var-idx))
;;           (else (loop frame-idx (+ var-idx 1)
;;                       (cons (cdr (car ctenv)) (cdr ctenv))))))
;;   (loop 0 0 ctenv))
;; (define (compile-assignment exp target linkage ctenv)
;;   (let* ((lexical-address (get-lexical-address (assignment-variable exp)
;;                                                ctenv))
;;          (set-var-key (if (null? lexical-address)
;;                           (assignment-variable exp)
;;                           lexical-address))
;;          (set-var-proc (if (null? lexical-address)
;;                            'set-variable-value!
;;                            'lexical-address-set!))
;;          (get-value-code
;;           (compile (assignment-value exp) 'val 'next ctenv)))
;;     (end-with-linkage
;;      linkage
;;      (preserving
;;       '(env)
;;       get-value-code
;;       (make-instruction-sequence '(env val) (list target)
;;                                  `((perform (op ,set-var-proc)
;;                                             (const ,set-var-key)
;;                                             (reg val)
;;                                             (reg env))
;;                                    (assign ,target (const ok))))))))
;; (define (lexical-address-set! address val env)
;;   (define (seek frame-idx var-idx env)
;;     (cond ((and (= frame-idx 0) (= var-idx 0))
;;            (set-cdr! (caar env) (list val)))
;;           ((not (= frame-idx 0))
;;            (seek (- frame-idx 1) var-idx (cdr env)))
;;           (else (seek 0 (- var-idx 1) (cons (cdr (car env)) (cdr env))))))
;;   (seek (car address) (cadr address) env))
;; (define (compile-application exp target linkage ctenv)
;;   (let ((proc-code (compile (operator exp) 'proc 'next ctenv))
;;         (operand-codes
;;          (map (lambda (operand) (compile operand 'val 'next ctenv))
;;               (operands exp))))
;;     (preserving '(env continue)
;;                 proc-code
;;                 (preserving '(proc continue)
;;                             (construct-arglist operand-codes)
;;                             (compile-procedure-call target linkage)))))
;; (define (scan-out-defines body)
;;   (let* ((def-expressions (definition-expressions body))
;;          (def-variables (definition-variables def-expressions))
;;          (def-values (definition-values def-expressions)))
;;     (make-let
;;      (map (lambda (var) (list var ''*unassigned*)) def-variables)
;;      (append
;;       (map (lambda (var val) (list 'set! var val))
;;            def-variables
;;            def-values)
;;       (non-definition-expressions body)))))
;; (define (definition-expressions body)
;;   (scan-expressions body (lambda (exp) (tagged-list? exp 'define))))
;; (define (non-definition-expressions body)
;;   (scan-expressions body (lambda (exp) (not (tagged-list? exp 'define)))))
;; (define (scan-expressions body predicate)
;;   (cond ((null? body) '())
;;         ((predicate (car body))
;;          (cons (car body) (scan-expressions (cdr body) predicate)))
;;         (else (scan-expressions (cdr body) predicate))))
;; (define (definition-values def-expressions)
;;   (map definition-value def-expressions))
;; (define (definition-variables def-expressions)
;;   (map definition-variable def-expressions))
;; (define (let? exp) (tagged-list? exp 'let))
;; (define (let-assignment-clauses exp) (cadr exp))
;; (define (let-assignment-variables assignments)
;;   (map car assignments))
;; (define (let-assignment-values assignments)
;;   (map cadr assignments))
;; (define (let-body exp) (cddr exp))
;; (define (let->combination exp)
;;   (if (not (named-let? exp))
;;       (let ((assignment-clauses (let-assignment-clauses exp)))
;;         (append
;;          (list (make-lambda (let-assignment-variables assignment-clauses)
;;                             (let-body exp)))
;;          (let-assignment-values assignment-clauses)))
;;       ;; Named let
;;       (let ((assignment-clauses (named-let-assignment-clauses exp)))
;;         (sequence->exp
;;          (list
;;           (make-procedure-definition
;;            (named-let-var exp)
;;            (let-assignment-variables assignment-clauses)
;;            (named-let-body exp))
;;           (append (list (named-let-var exp))
;;                   (let-assignment-values assignment-clauses)))))
;;       ))
;; (define (make-let assignment-clauses body)
;;   (append (list 'let assignment-clauses) body))
;; (define (compile-primitive-binary op exp target linkage ctenv)
;;   (if (not (null? (get-lexical-address op ctenv)))
;;       (compile-application exp target linkage ctenv)
;;       (end-with-linkage
;;        linkage
;;        (append-instruction-sequences
;;         (spread-arguments op (cdr exp) ctenv)
;;         (make-instruction-sequence
;;          '(arg1 arg2) (list target)
;;          `((assign ,target (op ,op) (reg arg1) (reg arg2))))))))
;; 5.5.7 Interfacing Compiled Code to the Evaluator
(define the-global-environment (setup-environment))
(define (start-eceval)
  (set! the-global-environment (setup-environment))
  (set-register-contents! eceval 'flag false)
  (start eceval))
(define (compile-and-go expression)
  (let ((instructions
         (assemble (statements
                    (compile expression 'val 'return))
                   eceval)))
    (set! the-global-environment (setup-environment))
    (set-register-contents! eceval 'val instructions)
    (set-register-contents! eceval 'flag true)
    (start eceval)))
;; Interpretation and compilation
;; Exercise 5.47
(define (compile-procedure-call target linkage)
  (let ((primitive-branch (make-label 'primitive-branch))
        (compiled-branch (make-label 'compiled-branch))
        (compound-branch (make-label 'compound-branch))
        (after-call (make-label 'after-call)))
    (let ((compiled-linkage
           (if (eq? linkage 'next) after-call linkage)))
      (append-instruction-sequences
       (make-instruction-sequence '(proc) '()
                                  `((test (op primitive-procedure?) (reg proc))
                                    (branch (label ,primitive-branch))
                                    (test (op compound-procedure?) (reg proc))
                                    (branch (label ,compound-branch))))
       (parallel-instruction-sequences
        (parallel-instruction-sequences
         (append-instruction-sequences
          compiled-branch
          (compile-proc-appl target compiled-linkage))
         (append-instruction-sequences
          primitive-branch
          (end-with-linkage
           compiled-linkage
           (make-instruction-sequence '(proc argl)
                                      (list target)
                                      `((assign ,target
                                                (op apply-primitive-procedure)
                                                (reg proc)
                                                (reg argl)))))))
        (append-instruction-sequences
         compound-branch
         (compound-proc-appl target linkage)))
       after-call))))
(define (compound-proc-appl target linkage)
  (cond ((and (eq? target 'val) (not (eq? linkage 'return)))
         (make-instruction-sequence '(proc) all-regs
                                    `((assign continue (label ,linkage))
                                      (save continue)
                                      (goto (reg compapp)))))
        ((and (not (eq? target 'val))
              (not (eq? linkage 'return)))
         (let ((proc-return (make-label 'proc-return)))
           (make-instruction-sequence '(proc) all-regs
                                      `((assign continue (label ,proc-return))
                                        (save continue)
                                        (goto (reg compapp))
                                        ,proc-return
                                        (assign ,target (reg val))
                                        (goto (label ,linkage))))))
        ((and (eq? target 'val) (eq? linkage 'return))
         (make-instruction-sequence '(proc continue) all-regs
                                    '((save continue)
                                      (goto (reg compapp)))))
        ((and (not (eq? target 'val)) (eq? linkage 'return))
         (error "return linkage, target not val -- COMPILE"
                target))))
;; Exercise 5.48
;; Exercise 5.59
(define eceval-rcepl
  (make-machine
   '(exp env val proc argl continue unev arg1 arg2 compapp)
   eceval-operations
   '(
     (assign compapp (label compound-apply))
     (branch (label external-entry))
     read-eval-print-loop
     (perform (op initialize-stack))
     (perform
      (op prompt-for-input) (const ";;; EC-Eval input:"))
     (assign exp (op read))
     (assign env (op get-global-environment))
     (assign continue (label print-result))
     (goto (label eval-dispatch))
     print-result
     (perform (op print-stack-statistics))
     (perform
      (op announce-output) (const ";;; EC-Eval value:"))
     (perform (op user-print) (reg val))
     (goto (label read-eval-print-loop))
     external-entry
     (perform (op initialize-stack))
     (assign env (op get-global-environment))
     (assign continue (label print-result))
     (goto (reg val))
     unknown-expression-type
     (assign val (const unknown-expression-type-error))
     (goto (label signal-error))
     unknown-procedure-type
     (restore continue)    ; clean up stack (from `apply-dispatch')
     (assign val (const unknown-procedure-type-error))
     (goto (label signal-error))
     signal-error
     (perform (op user-print) (reg val))
     (goto (label read-eval-print-loop))
     eval-dispatch
     ;; (test (op self-evaluating?) (reg exp))
     ;; (branch (label ev-self-eval))
     ;; (test (op variable?) (reg exp))
     ;; (branch (label ev-variable))
     ;; (test (op quoted?) (reg exp))
     ;; (branch (label ev-quoted))
     ;; (test (op assignment?) (reg exp))
     ;; (branch (label ev-assignment))
     ;; (test (op definition?) (reg exp))
     ;; (branch (label ev-definition))
     ;; (test (op if?) (reg exp))
     ;; (branch (label ev-if))
     ;; (test (op lambda?) (reg exp))
     ;; (branch (label ev-lambda))
     ;; (test (op begin?) (reg exp))
     ;; (branch (label ev-begin))
     ;; (test (op cond?) (reg exp))
     ;; (branch (label ev-cond-special))
     ;; (test (op let?) (reg exp))
     ;; (branch (label ev-let))
     ;; (test (op compile-and-run?) (reg exp))
     (branch (label ev-compile-and-run))
     ;; (test (op application?) (reg exp))
     ;; (branch (label ev-application))
     ;; (goto (label unknown-expression-type))
     ev-self-eval
     (assign val (reg exp))
     (goto (reg continue))
     ev-variable
     (assign val (op lookup-variable-value) (reg exp) (reg env))
     (goto (reg continue))
     ev-quoted
     (assign val (op text-of-quotation) (reg exp))
     (goto (reg continue))
     ev-lambda
     (assign unev (op lambda-parameters) (reg exp))
     (assign exp (op lambda-body) (reg exp))
     (assign val (op make-procedure)
             (reg unev) (reg exp) (reg env))
     (goto (reg continue))
     ev-application
     (save continue)
     (save env)
     (assign unev (op operands) (reg exp))
     (save unev)
     (assign exp (op operator) (reg exp))
     (assign continue (label ev-appl-did-operator))
     (goto (label eval-dispatch))
     ev-appl-did-operator
     (restore unev)                  ; the operands
     (restore env)
     (assign argl (op empty-arglist))
     (assign proc (reg val))         ; the operator
     (test (op no-operands?) (reg unev))
     (branch (label apply-dispatch))
     (save proc)
     ev-appl-operand-loop
     (save argl)
     (assign exp (op first-operand) (reg unev))
     (test (op last-operand?) (reg unev))
     (branch (label ev-appl-last-arg))
     (save env)
     (save unev)
     (assign continue (label ev-appl-accumulate-arg))
     (goto (label eval-dispatch))
     ev-appl-accumulate-arg
     (restore unev)
     (restore env)
     (restore argl)
     (assign argl (op adjoin-arg) (reg val) (reg argl))
     (assign unev (op rest-operands) (reg unev))
     (goto (label ev-appl-operand-loop))
     ev-appl-last-arg
     (assign continue (label ev-appl-accum-last-arg))
     (goto (label eval-dispatch))
     ev-appl-accum-last-arg
     (restore argl)
     (assign argl (op adjoin-arg) (reg val) (reg argl))
     (restore proc)
     (goto (label apply-dispatch))
     ev-compile-and-run
     (assign val (op assemble-eceval) (reg exp))
     (assign continue (label print-result))
     (goto (reg val))
     apply-dispatch
     (test (op primitive-procedure?) (reg proc))
     (branch (label primitive-apply))
     (test (op compound-procedure?) (reg proc))
     (branch (label compound-apply))
     (test (op compiled-procedure?) (reg proc))
     (branch (label compiled-apply))
     (goto (label unknown-procedure-type))
     primitive-apply
     (assign val (op apply-primitive-procedure)
             (reg proc)
             (reg argl))
     (restore continue)
     (goto (reg continue))
     compound-apply
     (assign unev (op procedure-parameters) (reg proc))
     (assign env (op procedure-environment) (reg proc))
     (assign env (op extend-environment)
             (reg unev) (reg argl) (reg env))
     (assign unev (op procedure-body) (reg proc))
     (goto (label ev-sequence))
     compiled-apply
     (restore continue)
     (assign val (op compiled-procedure-entry) (reg proc))
     (goto (reg val))
     ev-begin
     (assign unev (op begin-actions) (reg exp))
     (save continue)
     (goto (label ev-sequence))
     ev-sequence
     (assign exp (op first-exp) (reg unev))
     (test (op last-exp?) (reg unev))
     (branch (label ev-sequence-last-exp))
     (save unev)
     (save env)
     (assign continue (label ev-sequence-continue))
     (goto (label eval-dispatch))
     ev-sequence-continue
     (restore env)
     (restore unev)
     (assign unev (op rest-exps) (reg unev))
     (goto (label ev-sequence))
     ev-sequence-last-exp
     (restore continue)
     (goto (label eval-dispatch))
     ;; Conditionals
     ev-if
     (save exp)                    ; save expression for later
     (save env)
     (save continue)
     (assign continue (label ev-if-decide))
     (assign exp (op if-predicate) (reg exp))
     (goto (label eval-dispatch))  ; evaluate the predicate
     ev-if-decide
     (restore continue)
     (restore env)
     (restore exp)
     (test (op true?) (reg val))
     (branch (label ev-if-consequent))
     ev-if-alternative
     (assign exp (op if-alternative) (reg exp))
     (goto (label eval-dispatch))
     ev-if-consequent
     (assign exp (op if-consequent) (reg exp))
     (goto (label eval-dispatch))
     ;; Assignments
     ev-assignment
     (assign unev (op assignment-variable) (reg exp))
     (save unev)                   ; save variable for later
     (assign exp (op assignment-value) (reg exp))
     (save env)
     (save continue)
     (assign continue (label ev-assignment-1))
     (goto (label eval-dispatch))  ; evaluate the assignment value
     ev-assignment-1
     (restore continue)
     (restore env)
     (restore unev)
     (perform
      (op set-variable-value!) (reg unev) (reg val) (reg env))
     (assign val (const ok))
     (goto (reg continue))
     ;; Definitions
     ev-definition
     (assign unev (op definition-variable) (reg exp))
     (save unev)                   ; save variable for later
     (assign exp (op definition-value) (reg exp))
     (save env)
     (save continue)
     (assign continue (label ev-definition-1))
     (goto (label eval-dispatch))  ; evaluate the definition value
     ev-definition-1
     (restore continue)
     (restore env)
     (restore unev)
     (perform
      (op define-variable!) (reg unev) (reg val) (reg env))
     (assign val (const ok))
     (goto (reg continue))
     ev-cond-derived
     (assign exp (op cond->if) (reg exp))
     (goto (label eval-dispatch))
     ev-cond-special
     (assign unev (op cond-clauses) (reg exp))
     ev-cond-loop
     (assign exp (op first-cond-clauses) (reg unev))
     (assign unev (op rest-cond-caluses) (reg unev))
     (test (op is-cond-else-clause?) (reg exp))
     (branch (label ev-cond-actions))
     (save unev)
     (save exp)
     (save env)
     (save continue)
     (assign exp (op cond-predicate) (reg exp))
     (assign continue (label ev-cond-decide))
     (goto (label eval-dispatch))
     ev-cond-decide
     (restore continue)
     (restore env)
     (restore exp)
     (restore unev)
     (test (op true?) (reg val))
     (branch (label ev-cond-actions))
     (goto (label ev-cond-loop))
     ev-cond-actions
     (assign unev (op cond-actions) (reg exp))
     (save continue)
     (goto (label ev-sequence))
     ev-let
     (assign exp (op let->combination) (reg exp))
     (goto (label eval-dispatch))
     )))
(define (start-eceval-rcepl)
  (set! the-global-environment (setup-environment))
  (set-register-contents! eceval 'flag false)
  (start eceval))
;; Exercise 5.50
;; metacircular evaluator from sicp
(define metacircular-evaluator-source
  '(
    (begin
      (define apply-in-underlying-scheme apply)
      (define (list-of-values exps env)
        (if (no-operands? exps)
            '()
            (cons (eval (first-operand exps) env)
                  (list-of-values (rest-operands exps) env))))

      (define (eval-if exp env)
        (if (true? (eval (if-predicate exp) env))
            (eval (if-consequent exp) env)
            (eval (if-alternative exp) env)))

      (define (eval-sequence exps env)
        (cond ((last-exp? exps) (eval (first-exp exps) env))
              (else (eval (first-exps) env)
                    (eval-sequence (rest-exps exps) env))))

      (define (eval-assignment exp env)
        (set-variable-value! (assignment-variable exp)
                             (eval (assignment-value exp) env)
                             env)
        'ok)

      (define (eval-definition exp env)
        (define-variable! (define-variable exp)
          (eval (define-value exp) env)
          env)
        'ok)

      (define (self-evaluating? exp)
        (if (number? exp)
            true
            (if (string? exp) true false)))

      (define (variable? exp)
        (symbol? exp))

      (define (quoted? exp)
        (tagged-list? exp 'quote))

      (define (text-of-quotation exp) (cadr exp))

      (define (tagged-list? exp tag)
        (if (pair? exp)
            (eq? (car exp) tag)
            false))

      (define (assignment? exp)
        (tagged-list? exp 'set!))

      (define (assignment-variable exp) (cadr exp))
      (define (assignment-value exp) (caddr exp))

      (define (definition? exp)
        (tagged-list? exp 'define))

      (define (define-variable exp)
        (if (symbol? (cadr exp))
            (cadr exp)
            (caadr exp)))

      (define (define-value exp)
        (if (symbol? (cadr exp))
            (caddr exp)
            (make-lambda (cdadr exp)
                         (cddr exp))))

      (define (lambda? exp) (tagged-list? exp 'lambda))
      (define (lambda-parameters exp) (cadr exp))
      (define (lambda-body exp) (cddr exp))

      (define (make-lambda parameters body)
        (cons 'lambda (cons parameters body)))

      (define (if? exp) (tagged-list? exp 'if))
      (define (if-predicate exp) (cadr exp))
      (define (if-consequent exp) (caddr exp))
      (define (if-alternative exp)
        (if (not (null? (cdddr exp)))
            (cadddr exp)
            'false))

      (define (make-if predicate consequent alternative)
        (list 'if predicate consequent alternative))

      (define (begin? exp) (tagged-list? exp 'begin))
      (define (begin-actions exp) (cdr exp))
      (define (last-exp? seq) (null? (cdr seq)))
      (define (first-exp seq) (car seq))
      (define (rest-exp seq) (cdr seq))

      (define (sequence->exp seq)
        (cond ((null? seq) seq)
              ((last-exp? seq) (first-exp seq))
              (else (make-begin seq))))

      (define (application? exp) (pair? exp))
      (define (operator exp) (car exp))
      (define (operands exp) (cdr exp))
      (define (no-operands? ops) (null? ops))
      (define (first-operand ops) (car ops))
      (define (rest-operands ops) (cdr ops))

      (define (cond? exp) (tagged-list? exp 'cond))
      (define (cond-clauses exp) (cdr exp))
      (define (cond-else-clause? clause)
        (eq? (cond-predicate clause) 'else))
      (define (cond-predicate clause) (car clause))
      (define (cond-actions clause) (cdr clause))
      (define (cond->if exp) (expand-clauses (cond-clauses exp)))
      (define (expand-clauses clauses)
        (if (null? clauses)
            'false
            (let ((first (car clauses))
                  (rest (cdr clauses)))
              (if (cond-else-clause? first)
                  (if (null? rest)
                      (sequence->exp (cond-actions first))
                      (error ("ELSE clause isn't last -- COND->IF" clauses)))
                  (make-if (cond-predicate first)
                           (sequence->exp (cond-actions first))
                           (expand-clauses rest))))))

      (define (true? x)
        (not (false? x)))

      (define (false? x)
        (eq? x false))

      (define (make-procedure parameters body env)
        (list 'procedure parameters body env))

      (define (compound-procedure? p)
        (tagged-list? p 'procedure))

      (define (procedure-parameters p) (cadr p))
      (define (procedure-body p) (caddr p))
      (define (procedure-environment p) (cadddr p))

      (define (enclosing-environment env) (cdr env))
      (define (first-frame env) (car env))

      (define the-empty-environment '())

      (define (make-frame variables values)
        (cons variables values))

      (define (frame-variables frame) (car frame))
      (define (frame-values frame) (cdr frame))

      (define (add-binding-to-frame! var val frame)
        (set-car! frame (cons var (car frame)))
        (set-cdr! frame (cons val (cdr frame))))

      (define (extend-environment vars vals base-env)
        (if (= (length vars) (length vals))
            (cons (make-frame vars vals) base-env)
            (if (< (length vars) (length vals))
                (error "Too many arguments supplied" vars vals)
                (error "Too few arguments supplied" vars vals))))

      (define (lookup-variable-value var env)
        (define (env-loop env)
          (define (scan vars vals)
            (cond ((null? vars)
                   (env-loop (enclosing-environment env)))
                  ((eq? var (car vars))
                   (car vals))
                  (else (scan (cdr vars) (cdr vals)))))
          (if (eq? env the-empty-environment)
              (error "Unbound variable" var)
              (let ((frame (first-frame env)))
                (scan (frame-variables frame)
                      (frame-values frame)))))
        (env-loop env))

      (define (set-variable-value! var env)
        (define (env-loop env)
          (define (scan vars vals)
            (cond ((null? vars)
                   (env-loop (enclosing-environment env)))
                  ((eq? var (car vars))
                   (set-car! vals val))
                  (else (scan (cdr vars) (cdr vals)))))
          (if (eq? env the-empty-environment)
              (error "Unbound variable -- SET!" var)
              (let ((frame (first-frame env)))
                (scan (frame-variables frame)
                      (frame-values frame)))))
        (env-loop env))

      (define (define-variable! var val env)
        (let ((frame (first-frame env)))
          (define (scan vars vals)
            (cond ((null? vars)
                   (add-binding-to-frame! var val frame))
                  ((eq? var (car vars))
                   (set-car! vals val))
                  (else (scan (cdr vars) (cdr vals)))))
          (scan (frame-variables frame)
                (frame-values frame))))

      (define (eval exp env)
        (cond ((self-evaluating? exp) exp)
              ((variable? exp) (lookup-variable-value exp env))
              ((quoted? exp) (text-of-quotation exp))
              ((assignment? exp) (eval-assignment exp env))
              ((definition? exp) (eval-definition exp env))
              ((if? exp) (eval-if exp env))
              ((lambda? exp)
               (make-procedure (lambda-parameters exp)
                               (lambda-body exp)
                               env))
              ((begin? exp)
               (eval-sequence (begin-actions exp) env))
              ((cond? exp)  (eval (cond->if exp) env))
              ((application? exp)
               (apply (eval (operator exp) env)
                      (list-of-values (operands exp) env)))
              (else
               (error "Unknown expression type -- EVAL" exp))))

      (define (apply procedure arguments)
        (cond ((primitive-procedure? procedure)
               (meta-apply-primitive-procedure procedure arguments))
              ((compound-procedure? procedure)
               (eval-sequence
                (procedure-body procedure)
                (extend-environment
                 (procedure-parameters procedure)
                 arguments
                 (procedure-environment procedure))))
              (else
               (error "Unknown procedure type -- APPLY" procedure))))

      (define (setup-environment)
        (let ((initial-env
               (extend-environment (primitive-procedure-names)
                                   (primitive-procedure-objects)
                                   the-empty-environment)))
          (define-variable! 'true true initial-env)
          (define-variable! 'false false initial-env)
          initial-env))


      (define (primitive-procedure? proc) (tagged-list? proc 'primitive))
      (define (primitive-implementation proc) (cadr proc))

      (define primitive-procedures
        (list (list 'car car)
              (list 'cdr cdr)
              (list 'cons cons)
              (list 'null? null?)
              (list '+ +)
              (list '- -)
              (list '* *)
              (list '/ /)
              (list 'list list)
              (list '= =)))

      (define meta-map
        (lambda (f l)
          (if (null? l)
              '()
              (cons (f (car l)) (meta-map f (cdr l))))))

      (define (primitive-procedure-names)
        (meta-map car primitive-procedures))

      (define (primitive-procedure-objects)
        (meta-map (lambda (p) list 'primitive (cadr p))
             primitive-procedures))

      (define (meta-apply-primitive-procedure proc args)
        (apply-in-underlying-scheme
         (primitive-implementation proc) args))

      (define input-prompt "=> ")

      (define (driver-loop)
        (prompt-for-input input-prompt)
        (let ((input (read)))
          (let ((output (eval input the-global-environment)))
            (announce-output)
            (user-print output)))
        (driver-loop))

      (define (prompt-for-input string)
        (newline) (newline) (display string))

      (define (announce-output)
        (newline) (newline))

      (define (user-print object)
        (if (compound-procedure? object)
            (display (list 'compound-procedure
                           (procedure-parameters object)
                           (procedure-body object)
                           '<procedure-env>))
            (display object)))

      (define the-global-environment (setup-environment))
      (driver-loop))))
(define metacircular-evaluator-object
  (compile metacircular-evaluator-source 'val 'next))
(define metacircular-evaluator-machine
  (make-machine
   '(val env proc argl continue compapp)
   eceval-operations
   (cons '(assign env (op get-global-environment))
         (statements metacircular-evaluator-object))))
(define (start-metacircular-evaluator-machine)
  (start metacircular-evaluator-machine))
;; (metacircular-evaluator-machine 'trace-on!)
(start-metacircular-evaluator-machine)
