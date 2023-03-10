object Lc0002 {
  class ListNode(_x: Int = 0, _next: ListNode = null) {
    var next: ListNode = _next
    var x: Int = _x

    override def toString: String = {
      val sb = new StringBuilder()
      sb.append("(")
      sb.append(x)
      var currNode = next
      while (currNode != null) {
        sb.append(s", ${currNode.x}")
        currNode = currNode.next
      }
      sb.append(")")
      sb.result
    }
  }

  def addTwoNumbers(l1: ListNode, l2: ListNode): ListNode = {
    val result = new ListNode((l1.x + l2.x) % 10)
    var carry = if (l1.x + l2.x > 9) 1 else 0
    var currNode = result
    var l1x = l1.next
    var l2x = l2.next
    while (l1x != null && l2x != null) {
      val currX = l1x.x + l2x.x + carry
      carry = if (currX > 9) 1 else 0
      currNode.next = new ListNode(currX % 10)
      currNode = currNode.next
      l1x = l1x.next
      l2x = l2x.next
    }
    var ll = if (l1x == null) l2x else l1x
    while (ll != null) {
      val currX = ll.x + carry
      carry = if (currX > 9) 1 else 0
      currNode.next = new ListNode(currX % 10)
      currNode = currNode.next
      ll = ll.next
    }
    if (carry == 1) {
      currNode.next = new ListNode(1)
    }
    result
  }

  def main(args: Array[String]): Unit = {
    println(
      addTwoNumbers(
        new ListNode(2, new ListNode(4, new ListNode(3))),
        new ListNode(5, new ListNode(6, new ListNode(4)))
      )
    )
  }
}
