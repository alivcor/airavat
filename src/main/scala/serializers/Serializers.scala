package com.iresium.airavat

/*
 * Created by @alivcor (Abhinandan Dubey) on 2/3/21 
 * Licensed under the Mozilla Public License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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


