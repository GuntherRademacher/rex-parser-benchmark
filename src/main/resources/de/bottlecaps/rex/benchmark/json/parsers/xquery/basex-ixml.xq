import module namespace rip = 'rewrite-ixml-parse' at "src/main/resources/de/bottlecaps/rex/benchmark/json/parsers/xquery/rewrite-ixml-parse.xq";

declare variable $input external;

let $grammar := unparsed-text('src/main/resources/de/bottlecaps/rex/benchmark/json/parsers/xquery/json.ixml')
let $parser := invisible-xml($grammar)
return rip:rewrite-ixml-parse($parser($input), ())