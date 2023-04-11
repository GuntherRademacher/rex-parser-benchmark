import module namespace rrp = "rewrite-rex-parse" at "src/main/resources/de/bottlecaps/rex/benchmark/json/parsers/xquery/rewrite-rex-parse.xq";
declare namespace parser = "de/bottlecaps/rex/benchmark/json/parsers/xquery/RExLL";

declare variable $input external;

rrp:rewrite-rex-parse(parser:parse-json($input))