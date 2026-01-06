package com.cuongd.study.lcpis
package ch2

import scala.collection.mutable

object Ex211 {
  class ConcurrentBiMap[K, V] {
    private val keyToVal: mutable.Map[K, V] = mutable.Map[K, V]()
    private val valToKey: mutable.Map[V, K] = mutable.Map[V, K]()
    private val keyLock: AnyRef = new AnyRef
    private val valLock: AnyRef = new AnyRef

    def put(k: K, v: V): Option[(K, V)] = keyLock.synchronized {
      valLock.synchronized {
        val oldVal = keyToVal.put(k, v)
        val oldKey = if (oldVal.isDefined) valToKey.get(oldVal.get) else None
        valToKey.put(v, k)

        if (oldKey.isDefined) Some(oldKey.get, oldVal.get) else None
      }
    }

    def removeKey(k: K): Option[V] = removeKorV(k, keyToVal, valToKey)
    def removeValue(v: V): Option[K] = removeKorV(v, valToKey, keyToVal)

    def getValue(k: K): Option[V] = keyLock.synchronized { keyToVal.get(k) }
    def getKey(v: V): Option[K] = valLock.synchronized { valToKey.get(v) }

    def iterator: Iterator[(K, V)] = keyToVal.iterator

    private def removeKorV[A, B](KorV: A, removeFrom: mutable.Map[A, B],
                         removeAlso: mutable.Map[B, A]): Option[B] = keyLock.synchronized {
      valLock.synchronized {
        val old = removeFrom.remove(KorV)

        if (old.isDefined) removeAlso.remove(old.get)
        old
      }
    }
  }
}
