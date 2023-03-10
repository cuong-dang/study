object LC0004 {
  def findMedianSortedArrays(nums1: Array[Int], nums2: Array[Int]): Double = {
    val n = nums1.length + nums2.length
    if (n % 2 == 1) {
      kth(nums1, nums2, n / 2)
    } else {
      (kth(nums1, nums2, n / 2).toDouble + kth(nums1, nums2, n / 2 - 1)) / 2
    }
  }

  def kth(a: Array[Int], b: Array[Int], k: Int): Int = {
    if (a.isEmpty) return b(k)
    if (b.isEmpty) return a(k)

    var ax = a
    var bx = b
    if (b(b.length / 2) > a(a.length / 2)) {
      ax = b
      bx = a
    }
    if (k > ax.length / 2 + bx.length / 2) {
      kth(ax, bx.slice(bx.length / 2 + 1, bx.length), k - bx.length / 2 - 1)
    } else {
      kth(ax.slice(0, ax.length / 2), bx, k)
    }
  }

  def main(args: Array[String]): Unit = {
    println(findMedianSortedArrays(Array(0, 4, 8), Array(1, 2, 3, 5, 6, 7)))
  }
}
