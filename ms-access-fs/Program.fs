open System
open System.Data.OleDb

let con_info = 
    "Provider=Microsoft.ACE.OLEDB.12.0;" +
    "Data Source=TDB.accdb"

let insert_record () = 
    use con = 
        new OleDbConnection(con_info)
    let sql = 
        "INSERT INTO Person " + 
        "(FirstName, LastName, Email) " + 
        "Values ('ABC', 'DEF', 'RTR@CFC.COM')"
    use cmd = 
        new OleDbCommand(sql, con)
    try 
        con.Open()
        cmd.ExecuteNonQuery() |> ignore
        printfn "Inserted!"
    with ex ->
        printfn "%A" ex 
    
let read_records () = 
    use con = 
        new OleDbConnection(con_info)
    let sql = 
        "SELECT * FROM Person"
    use cmd = 
        new OleDbCommand(sql, con)
    try
        con.Open()
        use reader = 
            cmd.ExecuteReader()
        while reader.Read() do
            printfn "%s" (string reader.["FirstName"])
    with ex -> 
        printfn "%A" ex

[<EntryPoint>]
let main _ =
    insert_record ()
    read_records ()
    0
