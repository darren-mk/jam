module Main where

import qualified Control.Concurrent as CC -- (threadDelay)

scheduler :: IO () -> IO ()
scheduler task = do
    task
    CC.threadDelay (1000 * 1000) -- delay for 5 seconds
    scheduler task

main :: IO ()
main = scheduler (putStrLn "Task running")
