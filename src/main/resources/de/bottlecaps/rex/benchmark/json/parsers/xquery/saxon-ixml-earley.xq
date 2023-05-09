declare namespace sacks = 'http://nineml.com/ns/coffeesacks';

import module namespace rip = 'rewrite-ixml-parse' at "src/main/resources/de/bottlecaps/rex/benchmark/json/parsers/xquery/rewrite-ixml-parse.xq";

declare function local:invisible-xml($grammar as xs:string) as function(xs:string) as document-node()
{
  sacks:make-parser($grammar, map {'parser': 'Earley'})
};

declare variable $input external;

let $grammar := unparsed-text('src/main/resources/de/bottlecaps/rex/benchmark/json/parsers/xquery/json.ixml')
let $parser := local:invisible-xml($grammar)
return rip:rewrite-ixml-parse($parser($input), ())