package object avro

type KeyRFTag
type KeyRecordFormat[K] = RecordFormat[K] @@ KeyRFTag

type ValueRFTag
type ValueRecordFormat[V] = RecordFormat[V] @@ ValueRFTag

val carIdSchema: Schema = AvroSchema[CarId]
val carSpeedSchema: Schema = AvroSchema[CarSpeed]

implicit val carIdRF: KeyRecordFormat[CarId] = RecordFormat[CarId].taggedWith[KeyRFTag]
implicit val carSpeedRF: ValueRecordFormat[CarSpeed] = RecordFormat[CarSpeed].taggedWith[ValueRFTag]

 