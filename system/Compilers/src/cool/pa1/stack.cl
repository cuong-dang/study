(*
 *  CS164 Fall 94
 *
 *  Programming Assignment 1
 *    Implementation of a simple stack machine.
 *
 *  Skeleton file
 *)

class Main inherits IO {
   ai: A2I <- new A2I;
   m: StackMachine <- new StackMachine;

   main() : Object {
      let done: Bool <- false in
         while (not done) loop {
            out_string(">");
            let x: String <- in_string() in
               if (is_int(x)) then m.add((new IntCommand).init(ai.a2i(x))) else
               if (x = "+") then m.add(new PlusCommand) else
               if (x = "s") then m.add(new SwapCommand) else
               if (x = "e") then m.eval() else
               if (x = "d") then m.display() else
               if (x = "x") then done <- true else abort()
               fi fi fi fi fi fi;
         } pool
   };

   is_int(x: String): Bool {
      -- Since the input is promised to be well-formed. We only need to peek at
      -- the first digit.
      let r: Bool <- false, i: Int <- 0 in {
         while (i <= 9) loop {
            if (x.substr(0, 1) = ai.i2a(i)) then r <- true else false fi;
            i <- i + 1;
         } pool;
         r;
      }
   };
};

class StackCommand {
   execute(m: StackMachine): Object { 0 };

   str(): String { "" };
};

class IntCommand inherits StackCommand {
   ai: A2I <- new A2I;
   i: Int;

   init(x: Int): IntCommand {
      {
         i <- x;
         self;
      }
   };

   val(): Int { i };

   execute(m: StackMachine): Object { m.add((new IntCommand).init(i)) };

   str(): String { ai.i2a(i) };
};

class PlusCommand inherits StackCommand {
   execute(m: StackMachine): Object {
      case m.pop() of
         a: IntCommand => case m.pop() of
            b: IntCommand => m.add((new IntCommand).init(a.val() + b.val()));
         esac;
      esac
   };

   str(): String { "+" };
};

class SwapCommand inherits StackCommand {
   execute(m: StackMachine): Object {
      let x: StackCommand <- m.pop(), y: StackCommand <- m.pop() in {
         m.add(x);
         m.add(y);
      }
   };

   str(): String { "s" };
};

class StackMachine inherits IO {
   s: List <- new List;

   add(cmd: StackCommand): Object { s <- s.cons(cmd) };

   pop(): StackCommand {
      let x: StackCommand <- s.head() in {
         s <- s.tail();
         x;
      }
   };

   eval(): Object { pop().execute(self) };

   display(): Object {
      let t: List <- s in
         while (not t.isNil()) loop {
            out_string(t.head().str());
            out_string("\n");
            t <- t.tail();
         } pool
   };

};

class List {
   isNil() : Bool { true };

   head()  : StackCommand { { abort(); new StackCommand; } };

   tail()  : List { { abort(); self; } };

   cons(x : StackCommand) : List {
      (new Cons).init(x, self)
   };

};

class Cons inherits List {
   car : StackCommand;
   cdr : List;

   isNil() : Bool { false };

   head()  : StackCommand { car };

   tail()  : List { cdr };

   init(x : StackCommand, rest : List) : List {
      {
         car <- x;
         cdr <- rest;
         self;
      }
   };

};
