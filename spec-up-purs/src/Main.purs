module Main where

import Prelude
import Effect (Effect)
import Effect.Console (log)

love :: Int -> Int
love x = x * 123

main :: Effect Unit
main = do
  log "🍝"
