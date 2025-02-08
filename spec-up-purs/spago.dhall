{ name = "my-project"
, dependencies =
  [ "aff", "console", "datetime", "effect", "prelude", "spec" ]
, packages = ./packages.dhall
, sources = [ "src/**/*.purs", "test/**/*.purs" ] }
