open System
open Npgsql
open Dapper.FSharp
open Dapper.FSharp.PostgreSQL
open System.Data

Dapper.FSharp.PostgreSQL.OptionTypes.register()

type Person = {
    Id : Guid
    Username : string
    Email : string }

let personTable: QuerySource<Person> =
    table<Person>

let connectionString: string =
    "Host=localhost;Port=5432;Username=localuser;Password=localpass;Database=dapper_up_fs_db"

let conn : IDbConnection =
    new NpgsqlConnection(connectionString)

Console.WriteLine conn

let newPerson: Person =
    { Id = Guid.NewGuid()
    ; Username = "Roman"
    ; Email = "Provaznik" }

Console.WriteLine newPerson

let result =
    insert {
        into personTable
        value newPerson }
        |> conn.InsertAsync

Console.WriteLine result

printfn "Hello from F#"
