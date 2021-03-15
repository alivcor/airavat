/*
 * Created by @alivcor (Abhinandan Dubey) on 2/3/21 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify,
 *  merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software
 *  is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice, author's credentials and this
 * permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.iresium.airavat

import java.lang.reflect.Type
import com.google.gson._
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}



class ListSerializer extends JsonSerializer[Seq[Any]] {
    override def serialize(src: Seq[Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
        import scala.collection.JavaConverters._
        context.serialize(src.toList.asJava)
    }
}

class MapSerializer extends JsonSerializer[Map[Any,Any]] {
    override def serialize(src: Map[Any,Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
        import scala.collection.JavaConverters._
        context.serialize(src.asJava)
    }
}

class OptionSerializer extends JsonSerializer[Option[Any]] {
    override def serialize(src: Option[Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
        src match {
            case None => JsonNull.INSTANCE
            case Some(v) => context.serialize(v)
        }
    }
}

class DateTimeSerializer extends JsonSerializer[DateTime] {
    val DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC)

    override def serialize(src: DateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
        new JsonPrimitive(DATE_TIME_FORMATTER.print(src))
    }
}


