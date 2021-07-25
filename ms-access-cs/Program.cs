using System;
using System.Data.OleDb;

namespace Akida {
  class Program {
      static void Main(string[] args)
      {
        
        // Connection information
        const string con_info = 
            "Provider=Microsoft.ACE.OLEDB.12.0;Data Source=TDB.accdb";

        // Insertion
        void insertRecord() {   
          using (var con = new OleDbConnection(con_info)) {
            var sql = 
              "INSERT INTO Person " + 
              "(FirstName, LastName, Email) " + 
              "Values ('ABC', 'DEF', 'RTR@CFC.COM')";
            var cmd = new OleDbCommand(sql, con);
              try {
                con.Open();
                cmd.ExecuteNonQuery();
                Console.WriteLine("Inserted!");}
              catch (Exception ex) {
                Console.WriteLine(ex.Message);}}};

          // Read
          void readRecord() {   
            using (var con = new OleDbConnection(con_info)) {
              var sql = "SELECT * FROM Person";
              var cmd = new OleDbCommand(sql, con);
              try {
                con.Open();
                using (var reader = cmd.ExecuteReader()) {
                  while (reader.Read()) {
                    Console.WriteLine("{0}", reader["FirstName"].ToString());}}}
              catch (Exception ex) {
                Console.WriteLine(ex.Message);}}};

          // execution
          insertRecord();
          readRecord();
      }}}