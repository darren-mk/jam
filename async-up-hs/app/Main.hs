module Main where

import Control.Concurrent
import Control.Concurrent.Async

main :: IO ()
main = do
  res <- concurrently actionA actionB
  putStrLn "Hello, Haskell!"

actionA = do
  threadDelay 1001500
  putStrLn "action A result!"

actionB = do
  threadDelay 1000000
  putStrLn "action B result!"
