package utils

import play.api.libs.Crypto

trait EncryptionUtils {
  def enc(v: String): String = Crypto.encryptAES(v)

  def enc(v: (String, String)): (String, String) = (enc(v._1), enc(v._2))

  def dec(v: String): String = Crypto.decryptAES(v)

  def dec(v: (String, String)): (String, String) = (dec(v._1), dec(v._2))

  def dec(data: Map[String, String]): Map[String, String] = for ((key, value) <- data) yield (dec(key), dec(value))
}

object EncryptionUtilsHelper extends EncryptionUtils
