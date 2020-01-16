package com.griddynamics.kafka.connector

import org.apache.hadoop.fs.Path
import org.scalacheck.Gen
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Success

class FSPathToEventIdMapperTest extends FlatSpec with Matchers with MockFactory {
  "mapper" should "properly parse path \\d+_\\d+ file name" in {
    val timestamp = Gen.posNum[Long].sample.get
    val internalId = Gen.chooseNum(0L, Long.MaxValue).sample.get

    val expectedIdentifier = EventIdentifier(timestamp, internalId, None)

    val path = new Path(s"${timestamp}_$internalId")

    assert(Success(expectedIdentifier) === FSPathToEventIdMapper().map(path))
  }

  "mapper" should "properly parse path \\d+_\\d+_\\s file name" in {
    val timestamp = Gen.posNum[Long].sample.get
    val internalId = Gen.chooseNum(0L, Long.MaxValue).sample.get
    val instanceName = Gen.alphaStr.sample.get

    val expectedIdentifier = EventIdentifier(timestamp, internalId, Option(instanceName))

    val path = new Path(s"${timestamp}_${internalId}_$instanceName")

    assert(Success(expectedIdentifier) === FSPathToEventIdMapper().map(path))
  }

  "mapper" should "return failure in case if there are more than 3 groups" in {
    val timestamp = Gen.posNum[Long].sample.get
    val internalId = Gen.chooseNum(0L, Long.MaxValue).sample.get
    val instanceName1 = Gen.alphaStr.sample.get
    val instanceName2 = Gen.alphaStr.sample.get

    val path = new Path(s"${timestamp}_${internalId}_${instanceName1}_${instanceName2}")

    assert(FSPathToEventIdMapper().map(path).isFailure)
  }

  "mapper" should "return failure in case if path totally violates pattern" in {
    val pathName = Gen.alphaStr.sample.get

    val path = new Path(pathName)

    assert(FSPathToEventIdMapper().map(path).isFailure)
  }


}
