module Main where

import Prelude

import Effect (Effect)
import Effect.Console (log)

mult :: Int -> Int -> Int
mult a b = a * b

main :: Effect Unit
main = do
  log "🍝"
