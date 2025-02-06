module Lib
    ( someFunc
    , myName
    , adder
    ) where

someFunc :: IO ()
someFunc = putStrLn "someFunc"

myName :: String
myName = "darren kim"

adder :: Int -> Int -> Int
adder a b = a + b
