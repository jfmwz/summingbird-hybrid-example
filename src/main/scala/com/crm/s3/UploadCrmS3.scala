package com.crm.s3

import awscala.Region
import awscala.s3.{S3Object, S3ObjectSummary, Bucket, S3}
import org.joda.time.DateTime


object UploadCrmS3 extends App {

  implicit val region = awscala.Region.Ireland

  implicit val s3 = S3("AKIAI665RYMKQTNH5UPA", "Znxlf16pHGHGMl9olwwLID4Vik4OxELViDDc2sLM")


  val buckets: Seq[Bucket] = s3.buckets
  //val bucket: Bucket = s3.createBucket("lotto.sandbox1")
  val bucket: Bucket = s3.bucket("lotto.sandbox").getOrElse(s3.createBucket("lotto.sandbox1"))
  val summaries: Seq[S3ObjectSummary] = bucket.objectSummaries

  bucket.put("sample.txt", new java.io.File("C:\\Users\\jose\\git\\summingbird-hybrid-example\\build.sbt"))

  /*
  val s3obj: Option[S3Object] = bucket.getObject("sample.txt")

  s3obj.foreach { obj =>
    obj.publicUrl // http://unique-name-xxx.s3.amazonaws.com/sample.txt
    obj.generatePresignedUrl(DateTime.now.plusMinutes(10)) // ?Expires=....
    bucket.delete(obj) // or obj.destroy()
  }
  */
}