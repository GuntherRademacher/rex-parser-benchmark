import module namespace parser = "JSON" at "build/generated/rex/main/de/bottlecaps/rex/benchmark/json/parsers/xquery/json-lalr.xquery";
import module namespace rrp = "rewrite-rex-parse" at "src/main/resources/de/bottlecaps/rex/benchmark/json/parsers/xquery/rewrite-rex-parse.xq";

declare variable $input external;

rrp:rewrite-rex-parse(parser:parse-json($input))