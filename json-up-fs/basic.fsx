#r "nuget: FSharp.Json, 0.4.1"
open FSharp.Json

// Your record type
type RecordType = {
    stringMember: string
    intMember: int }

let datum: RecordType = { 
  stringMember = "The string"; 
  intMember = 123 }

let json = Json.serialize datum

let s = 
  """
  {
    "stringMember": "The string",
    "intMember": 123, 
    "unknown": 123,
    "garbage": "abc"
  }
  """

let deserialized: RecordType option =
  try Some (Json.deserialize<RecordType> s)
  with | _ -> None
