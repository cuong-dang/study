;1.1 THE ELEMENTS OF PROGRAMMING
;1.1.1 Expressions
;1.1.2 Naming and the Environment
;1.1.3 Evaluating Combinations
;1.1.4 Compound Procedures
(define (square x) (* x x))
(define (sum-of-squares x y)
  (+ (square x) (square y)))
(define (f a)
  (sum-of-squares (+ a 1) (* a 2)))

;1.1.5 The Substitution Model for Procedure Application
; normal-order evaluation: fully expand and then reduce
; applicative-order evaluation: evaluate the arguments and then apply

;1.1.6 Conditional Expressions and Predicates
(define (abs x)
  (cond ((> x 0) x)
	((= x 0) 0)
	((< x 0) (- x))))
(define (abs x)
  (cond ((< x 0) (- x))
	(else x)))
(define (abs x)
  (if (< x 0)
      (- x)
      x))
(define (>= x y)
  (or (> x y) (= x y)))
(define (>= x y)
  (not (< x y)))

;Exercise 1.1.
; 10
; 12
; 8
; 3
; 6
; a
; b
; 19
; #f
; 4
; 16
; 6
; 16

;Exercise 1.2.
; (/ (+ 5 4 (- 2 (- 3 (+ 6 (/ 4 5)))))
;    (* 3 (- 6 2) (- 2 7)))

;Exercise 1.3.
(define (ex-1-3 a b c)
  (cond ((and (>= b a) (>= c a)) (sum-of-squares b c))
	((and (>= a b) (>= c b)) (sum-of-squares a c))
	(else (sum-of-squares a b))))

;Exercise 1.4.
(define (a-plus-abs-b a b)
  ((if (> b 0) + -) a b))

;Exercise 1.5.
(define (p) (p))
(define (test x y)
  (if (= x 0)
      0
      y))
; - applicative-order evaluation: infinite loop because arguments are evaluated
;   before procedure body is applied
; - normal-order evaluation: 0 because (p) is not evaluated if not needed

;1.1.7 Example: Square Roots by Newton's Method
(define (sqrt-iter guess x)
  (if (good-enough? guess x)
      guess
      (sqrt-iter (improve guess x)
		 x)))
(define (improve guess x)
  (average guess (/ x guess)))
(define (average x y)
  (/ (+ x y) 2))
(define (good-enough? guess x)
  (< (abs (- (square guess) x)) 0.001))
(define (sqrt x)
  (sqrt-iter 1.0 x))

;Exercise 1.7.
(define (good-enough? guess x)
  (< (/ (improved-guess-distance guess x) guess) 0.001))
(define (improved-guess-distance guess x)
  (abs (- (improve guess x) guess)))

;Exercise 1.8.
(define (cube-root x)
  (cube-root-iter 1.0 x))
(define (cube-root-iter guess x)
  (if (cube-root-good-enough? guess x)
      guess
      (cube-root-iter (cube-root-improve guess x) x)))
(define (cube-root-good-enough? guess x)
  (< (/ (cube-root-improved-guess-distance guess x) guess) 0.001))
(define (cube-root-improved-guess-distance guess x)
  (abs (- (cube-root-improve guess x) guess)))
(define (cube-root-improve y x)
  (/ (+ (/ x (square y)) (* 2 y)) 3))

;1.1.8 Procedures as Black-Box Abstractions

;1.2 Procedures and the Processes They Generate
(define (factorial n)
  (if (= n 1)
      1
      (* n (factorial (- n 1)))))

(define (factorial n)
  (fact-iter 1 1 n))
(define (fact-iter product counter max-count)
  (if (> counter max-count)
      product
      (fact-iter (* product counter)
		 (+ counter 1)
		 max-count)))
(define (factorial n)
  (define (iter product counter)
    (if (> counter n)
	product
	(iter (* product counter)
	      (+ counter 1))))
  (iter 1 1))

;Exercise 1.9.
;;first procedure
;;(+ 4 5)
;;(inc (+ (3 5)))
;;(inc (inc (+ 2 5)))
;;(inc (inc (inc (+ 1 5))))
;;(inc (inc (inc (inc (+ 0 5)))))
;;(inc (inc (inc (inc 5))))
;;(inc (inc (inc 6)))
;;(inc (inc 7))
;;(inc 8)
;;9

;;second procedure
;;(+ 4 5)
;;(+ 3 6)
;;(+ 2 7)
;;(+ 1 8)
;;(+ 0 9)
;;9

;Exercise 1.10.
(define (A x y)
  (cond ((= y 0) 0)
	((= x 0) (* 2 y))
	((= y 1) 2)
	(else (A (- x 1)
		 (A x (- y 1))))))
;;(A 1 10) = 2^10
;;(A 2 4) = 2^16
;;(A 3 3) = 2^16
;;(f n) = (A 0 n) = 2 * n
;;(g n) = (A 1 n) = 2 ^ n
;;(h n) = (A 2 n) = 2 ^ 2 ^ 2 ^ ... (n number of 2)

;1.2.2 Tree Recursion
(define (fib n)
  (cond ((= n 0) 0)
	((= n 1) 1)
	(else (+ (fib (- n 1))
		 (fib (- n 2))))))
(define (fib n)
  (fib-iter 1 0 n))
(define (fib-iter a b count)
  (if (= count 0)
      b
      (fib-iter (+ a b) a (- count 1))))
;;Example: Counting change
(define (count-change amount)
  (cc amount 5))
(define (cc amount kinds-of-coins)
  (cond ((= amount 0) 1)
	((or (< amount 0) (= kinds-of-coins 0)) 0)
	(else (+ (cc amount (- kinds-of-coins 1))
		 (cc (- amount (first-denomination kinds-of-coins))
		     kinds-of-coins)))))
(define (first-denomination kinds-of-coins)
  (cond ((= kinds-of-coins 1) 1)
	((= kinds-of-coins 2) 5)
	((= kinds-of-coins 3) 10)
	((= kinds-of-coins 4) 25)
	((= kinds-of-coins 5) 50)))
;;Exercise 1.11.
(define (f n)
  (if (< n 3) n
      (+ (f (- n 1))
	 (* 2 (f (- n 2)))
	 (* 3 (f (- n 3))))))
(define (iterative-f n)
  (iter-f n n 2 1 0))
(define (iter-f n res fn-1 fn-2 fn-3)
  (if (< n 3) res
      (iter-f (- n 1)
	      (+ fn-1
		 (* 2 fn-2)
		 (* 3 fn-3))
	      (+ fn-1
		 (* 2 fn-2)
		 (* 3 fn-3))
	      fn-1
	      fn-2)))
;;Exercise 1.12.
(define (pascal-triangle i j)
  (cond ((= j 0) 1)
	((= i j) 1)
	(else (+ (pascal-triangle (- i 1) (- j 1))
		 (pascal-triangle (- i 1) j)))))
;;Exercise 1.13.
;1.2.3 Orders of Growth
;;Exercise 1.14.
;;Exercise 1.15.
;1.2.4 Exponentiation
(define (expt b n)
  (if (= n 0) 1
      (* b (expt b (- n 1)))))
(define (expt b n)
  (expt-iter b n 1))
(define (expt-iter b counter product)
  (if (= counter 0)
      product
      (expt-iter b
		 (- counter 1)
		 (* b product))))
(define (fast-expt b n)
  (cond ((= n 0) 1)
	((even? n) (square (fast-expt b (/ n 2))))
	(else (* b (fast-expt b (- n 1))))))
(define (even? n)
  (= (remainder n 2) 0))
(define (fast-expt b n)
  (fast-expt-iter b n 1 0))
(define (fast-expt-iter b n product curr-expt)
  (cond ((= n 0) product)
	((= curr-expt n) product)
	((= curr-expt 0) (fast-expt-iter b n b 1))
	((> n (* curr-expt 2)) (fast-expt-iter b n (square product) (* curr-expt 2)))
	(else (fast-expt-iter b n (* b product) (+ curr-expt 1)))))
(define (fast-expt b n)
  (fast-expt-iter b n 1))
(define (fast-expt-iter b n a)
  (cond ((= n 0) a)
	((even? n) (fast-expt-iter (square b) (/ n 2) a))
	(else (fast-expt-iter b (- n 1) (* b a)))))
;;Exercise 1.17.
(define (double x) (* x 2))
(define (halve x) (/ x 2))
(define (fast-mult a b)
  (cond ((= b 0) 0)
	((even? b) (double (fast-mult a (halve b))))
	(else (+ a (fast-mult a (- b 1))))))
;;Exercise 1.18.
(define (fast-mult a b)
  (fast-mult-iter a b 0))
(define (fast-mult-iter a b s)
  (cond ((= b 0) s)
	((even? b) (fast-mult-iter (double a) (halve b) s))
	(else (fast-mult-iter a (- b 1) (+ a s)))))
;;Exercise 1.19.
(define (fib n)
  (fib-iter 1 0 0 1 n))
(define (fib-iter a b p q count)
  (cond ((= count 0) b)
	((even? count)
	 (fib-iter a
		   b
		   (+ (square p) (square q))
		   (+ (* 2 p q) (square q))
		   (/ count 2)))
	(else (fib-iter (+ (* b q) (* a q) (* a p))
			(+ (* b p) (* a q))
			p
			q
			(- count 1)))))
;1.2.5 Greatest Common Divisors
(define (gcd a b)
  (if (= b 0)
      a
      (gcd b (remainder a b))))
;1.2.6 Example: Testing for Primality
;;Searching for divisors
(define (smallest-divisor n)
  (find-divisor n 2))
(define (find-divisor n test-divisor)
  (cond ((> (square test-divisor) n) n)
	((divides? test-divisor n) test-divisor)
	(else (find-divisor n (+ test-divisor 1)))))
(define (divides? a b)
  (= (remainder b a) 0))
(define (prime? n)
  (= n (smallest-divisor n)))
;;The Fermat test
(define (expmod base exp m)
  (cond ((= exp 0) 1)
	((even? exp)
	 (remainder (square (expmod base (/ exp 2) m))
		    m))
	(else
	 (remainder (* base (expmod base (- exp 1) m))
		    m))))
(define (fermat-test n)
  (define (try-it a)
    (= (expmod a n n) a))
  (try-it (+ (random (- n 1)))))
(define (fast-prime? n times)
  (cond ((= times 0) true)
	((fermat-test n) (fast-prime? n (- times 1)))
	(else false)))
;;Probabilistic methods
;;Exercise 1.21.
;;Exercise 1.22.
(define (timed-prime-test n)
  (newline)
  (display n)
  (start-prime-test n (runtime)))
(define (start-prime-test n start-time)
  (if (prime? n)
      (report-prime (- (runtime) start-time))))
(define (report-prime elapsed-time)
  (display " *** ")
  (display elapsed-time))
(define (search-for-primes start-range end-range)
  (search-for-primes-worker start-range end-range '()))
(define (search-for-primes-worker start-range end-range result)
  (cond ((> start-range end-range) result)
	((even? start-range) (search-for-primes-worker (+ start-range 1) end-range result))
	(else (if (fast-prime? start-range 10)
		  (search-for-primes-worker (+ start-range 2) end-range (cons start-range result))
		  (search-for-primes-worker (+ start-range 2) end-range result)))))
;;Exercise 1.23.
(define (next n)
  (if (= n 2) 3 (+ n 2)))
(define (find-divisor n test-divisor)
  (cond ((> (square test-divisor) n) n)
	((divides? test-divisor n) test-divisor)
	(else (find-divisor n (next test-divisor)))))
;;Exercise 1.24
(define (start-prime-test n start-time)
  (if (fast-prime? n 10)
      (report-prime (- (runtime) start-time))))
;;Exercise 1.25.
;;Exercise 1.26.
;;Exercise 1.27.
(define (fool-fermat? n)
  (fool-fermat?-worker n 1))
(define (fool-fermat?-worker n a)
  (cond ((= a n) #t)
	((= (expmod a n n) (remainder a n))
	 (fool-fermat?-worker n (+ a 1)))
	(else #f)))
;;Exercise 1.28.
(define (expmod base exp m)
  (cond ((= exp 0) 1)
	((even? exp)
	 (let ((squared-root (square (expmod base (/ exp 2) m))))
	   (if (= squared-root 1)
	       0
	       (remainder squared-root m))))
	(else (remainder (* base (expmod base (- exp 1) m)) m))))
(define (miller-rabin-test n)
  (miller-rabin-test-worker n (- n 1)))
(define (miller-rabin-test-worker n a)
  (define (try-it a)
    (or (= (expmod a (- n 1) n) 1)
	(= (expmod a (- n 1) n) 0)))
  (cond ((= a 0) #t)
	((try-it a) (miller-rabin-test-worker n (- a 1)))
	(else #f)))
;Lecture 2
(define (move-tower size from to extra)
  (cond ((= size 0) '())
	(else (move-tower (- size 1) from extra to)
	      (print-move from to)
	      (move-tower (- size 1) extra to from))))
(define (print-move from to)
  (newline)
  (display "Move top disk from ")
  (display from)
  (display " to ")
  (display to))
;1.3 Formulating Abstractions with Higher-Order Procedures
(define (cube x) (* x x x))
;;1.3.1 Procedures as Arguments
(define (sum-integers a b)
  (if (> a b)
      0
      (+ a (sum-integers (+ a 1) b))))
(define (sum-cubes a b)
  (if (> a b)
      0
      (+ (cube a) (sum-integers (+ a 1) b))))
(define (pi-sum a b)
  (if (> a b)
      0
      (+ (/ 1.0 (* a (+ a 2))) (pi-sum (+ a 4) b))))
(define (sum term a next b)
  (if (> a b)
      0
      (+ (term a)
	 (sum term (next a) next b))))
(define (inc n) (+ n 1))
(define (sum-cubes a b)
  (sum cube a inc b))
(define (identity x) x)
(define (sum-integers a b)
  (sum identity a inc b))
(define (pi-sum a b)
  (define (pi-term x)
    (/ 1.0 (* x (+ x 2))))
  (define (pi-next x)
    (+ x 4))
  (sum pi-term a pi-next b))
(define (integral f a b dx)
  (define (add-dx x) (+ x dx))
  (* (sum f (+ a (/ dx 2.0)) add-dx b)
     dx))
;;Exercise 1.29.
(define (simpson-integral f a b n)
  (define h (/ (- b a) n))
  (define (term n)
    (f (+ a (* n h))))
  (define (iter f a b n count res)
    (cond ((= count -1) (* (/ h 3.0) res))
	  ((or (= count 0) (= count n)) (iter f a b n (- count 1) (+ (term count) res)))
	  ((even? count) (iter f a b n (- count 1) (+ (* 2 (term count)) res)))
	  (else (iter f a b n (- count 1) (+ (* 4 (term count)) res)))))
  (iter f a b n n 0))
(define (simpson-integral f a b n)
  (define h (/ (- b a) n))
  (define (y k)
    (f (+ a (* k h))))
  (define (term x)
    (cond ((or (= x 0) (= x n)) (y x))
	  ((even? x) (* 2 (y x)))
	  (else (* 4 (y x)))))
  (* (/ h 3.0) (sum term 0 inc n)))
;;Exercise 1.30.
(define (sum term a next b)
  (define (iter a result)
    (if (> a b)
	result
	(iter (next a) (+ (term a) result))))
  (iter a 0))
;;Exercise 1.31.
;;;a.
(define (product term a next b)
  (define (iter a result)
    (if (> a b)
	result
	(iter (next a) (* (term a) result))))
  (iter a 1))
(define (factorial n)
  (product identity 1 inc n))
(define (pi-product b)
  (define (term a)
    (/ (* a (+ a 2))
       (square (+ a 1))))
  (define (next a)
    (+ 2 a))
  (* 4.0 (product term 2 next b)))
;;;b.
(define (product term a next b)
  (if (> a b)
      1
      (* (term a) (product term (next a) next b))))
;;Exercise 1.32.
;;;a.
(define (accumulate combiner null-value term a next b)
  (if (> a b)
      null-value
      (combiner (term a) (accumulate combiner null-value term (next a) next b))))
(define (sum term a next b)
  (accumulate + 0 term a next b))
(define (product term a next b)
  (accumulate * 1 term a next b))
;;;b.
(define (accumulate combiner null-value term a next b)
  (define (iter a result)
    (if (> a b)
	result
	(iter (next a) (combiner (term a) result))))
  (iter a null-value))
;;Exercise 1.33.
(define (filtered-accumulate combiner null-value term a next b filter)
  (define (iter a result)
    (if (> a b)
	result
	(iter (next a)
	      (if (filter a)
		  (combiner (term a) result)
		  result))))
  (iter a null-value))
;;;a.
(define (sum-of-squared-primes a b)
  (filtered-accumulate + 0 square a inc b prime?))
;;;b.
(define (sum-of-relative-primes n)
  (define (filter x)
    (and (< x n)
	 (= 1 (gcd x n))))
  (filtered-accumulate + 0 identity 1 inc n filter))
;;1.3.2 Constructing Procedures Using lambda
(define (pi-sum a b)
  (sum (lambda (x) (/ 1.0 (* x (+ x 2))))
       a
       (lambda (x) (+ x 4))
       b))
(define (integral f a b dx)
  (* (sum f
	  (+ a (/ dx 2.0))
	  (lambda (x) (+ x dx))
	  b)
     dx))
;;;Using let to create local variables
(define (f x y)
  (define (f-helper a b)
    (+ (* x (square a))
       (* y b)
       (* a b)))
  (f-helper (+ 1 (* x y))
	    (- 1 y)))
(define (f x y)
  (let ((a (+ 1 (* x y)))
	(b (- 1 y)))
    (+ (* x (square a))
       (* y b)
       (* a b))))
;;Exercise 1.34.
;;;Will cause error because the procedure will ask to evaluate (2 2)
;;1.3.3 Procedures as General Methods
;;;Finding roots of equations by the half-interval method
(define (search f neg-point pos-point)
  (let ((midpoint (average neg-point pos-point)))
    (if (close-enough? neg-point pos-point)
	midpoint
	(let ((test-value (f midpoint)))
	  (cond ((positive? test-value)
		 (search f neg-point midpoint))
		((negative? test-value)
		 (search f midpoint pos-point))
		(else midpoint))))))
(define (close-enough? x y)
  (< (abs (- x y)) 0.001))
(define (half-interval-method f a b)
  (let ((a-value (f a))
	(b-value (f b)))
    (cond ((and (negative? a-value) (positive? b-value))
	   (search f a b))
	  ((and (negative? b-value) (positive? a-value))
	   (search f b a))
	  (else
	   (error "Values are not of opposite sign" a b)))))
;;;Finding fixed points of functions
(define tolerance 0.00001)
(define (fixed-point f first-guess)
  (define (close-enough? v1 v2)
    (< (abs (- v1 v2)) tolerance))
  (define (try guess)
    (let ((next (f guess)))
      (display "guessing... ")
      (display guess)
      (newline)
      (if (close-enough? guess next)
	  next
	  (try next))))
  (try first-guess))
(define (sqrt x)
  (fixed-point (lambda (y) (/ x y))
	       1.0))
(define (sqrt x)
  (fixed-point (lambda (y) (average y (/ x y)))
	       1.0))
;;Exercise 1.35.
(define golden-ratio
  (fixed-point (lambda (x) (+ 1 (/ 1 x))) 1.0))
;;Exercise 1.36.
(define sol
  (fixed-point (lambda (x) (/ (log 1000) (log x))) 1.1))
(define sol-avg
  (fixed-point (lambda (x) (average x (/ (log 1000) (log x)))) 1.1))
;;Exercise 1.37.
;;;a.
(define (cont-frac n d k)
  (define (helper i)
    (if (= i k)
	(/ (n i) (d i))
	(/ (n i) (+ (d i) (helper (+ i 1))))))
  (helper 1))
;;;b.
(define (cont-frac n d k)
  (define (helper i res)
    (if (= i 0)
	res
	(helper (- i 1)
		(/ (n i)
		   (+ (d i) res)))))
  (helper k 0))
;;Exercise 1.38.
(define (euler-e k)
  (+ (cont-frac (lambda (i) 1.0)
		(lambda (i)
		  (cond ((or (= i 1) (= i 2)) i)
			((= (modulo (- i 2) 3) 0) (+ i 2))
			(else 1)))
		k)
     2))
;;Exercise 1.39.
(define (tan-cf x k)
  (cont-frac (lambda (i)
	       (if (= i 1.0) x (- (square x))))
	     (lambda (i) (- (* 2.0 i) 1.0))
	     k))
;;1.3.4 Procedures as Returned Values
(define (average-damp f)
  (lambda (x) (average x (f x))))
(define (sqrt x)
  (fixed-point (average-damp (lambda (y) (/ x y)))
	       1.0))
(define (cube-root x)
  (fixed-point (average-damp (lambda (y) (/ x (square y))))
	       1.0))
;;;Newton's method
(define (deriv g)
  (lambda (x)
    (/ (- (g (+ x dx)) (g x))
       dx)))
(define dx 0.00001)
(define (newton-transform g)
  (lambda (x)
    (- x (/ (g x) ((deriv g) x)))))
(define (newtons-method g guess)
  (fixed-point (newton-transform g) guess))
(define (sqrt x)
  (newtons-method (lambda (y) (- (square y) x))
		  1.0))
;;;Abstractions and first-class procedures
(define (fixed-point-of-transform g transform guess)
  (fixed-point (transform g) guess))
(define (sqrt x)
  (fixed-point-of-transform (lambda (y) (/ x y))
			    average-damp
			    1.0))
(define (sqrt x)
  (fixed-point-of-transform (lambda (y) (- (square y) x))
			    newton-transform
			    1.0))
;;Exercise 1.40.
(define (cubic a b c)
  (lambda (x) (+ (* x x x)
		 (* a x x)
		 (* b x)
		 c)))
;;Exercise 1.41.
;;21
;;Exercise 1.42.
(define (compose f g)
  (lambda (x) (f (g x))))
;;Exercise 1.43.
(define (repeated f n)
  (if (= n 1)
      f
      (repeated (compose f f) (- n 1))))
;;Exercise 1.44.
(define (smooth f)
  (lambda (x)
    (/ (+ (f (- x dx))
	  (f x)
	  (f (+ x dx)))
       3)))
(define (smooth-n-fold f n)
  ((repeated smooth n) f))
;;Exercise 1.45.
(define (nth-root n x)
  (define f (lambda (y) (/ x (expt y (- n 1)))))
  (fixed-point ((repeated average-damp 2) f) 1.0))
;;;Not yet able to figure out how many times damping is necessary.
;;Exercise 1.46.
(define (iterative-improve good-enough? improve-guess)
  (define (iter guess)
    (if (good-enough? guess)
	guess
	(iter (improve-guess guess))))
  iter)
(define (sqrt x)
  (define (good-enough? guess)
    (< (abs (- (square guess) x)) 0.001))
  (define (improve-guess guess)
    (average guess (/ x guess)))
  ((iterative-improve good-enough? improve-guess) 1.0))
(define (fixed-point f first-guess)
  (define (good-enough? guess)
    (let ((next (f guess)))
      (< (abs (- guess next)) tolerance)))
  (define (improve-guess guess)
    (f guess))
  ((iterative-improve good-enough? improve-guess) first-guess))