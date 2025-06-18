open System
open Npgsql
open Npgsql.FSharp
open System.Data

type Person = {
    Id : Guid
    Username : string
    Email : string }

let connectionString: string =
    "Host=localhost;Port=5432;Username=localuser;Password=localpass;Database=dapper_up_fs_db"

let conn : IDbConnection =
    new NpgsqlConnection(connectionString)

let newPerson: Person =
    { Id = Guid.NewGuid()
    ; Username = "Roman"
    ; Email = "Provaznik" }

let getAllPersons (connectionString: string) : Person list =
    connectionString
    |> Sql.connect
    |> Sql.query "SELECT * FROM people" // FS and LIB CANNOT DETECT THIS
    |> Sql.execute (fun read ->
        {
            Id = read.uuid "id"
            Username = read.text "name" // FS and LIB CANNOT DETECT THIS
            Email = read.text "email?" // FS and LIB CANNOT DETECT THIS
        })

Console.WriteLine (getAllPersons)

printfn "Hello from F#"
