import module namespace rrsgp = "rewrite-rex-scannerless-glr-parse" at "src/main/resources/de/bottlecaps/rex/benchmark/json/parsers/xquery/rewrite-rex-scannerless-glr-parse.xq";
declare namespace parser = "de/bottlecaps/rex/benchmark/json/parsers/xquery/RExScannerlessGLR";

declare variable $input external;

rrsgp:rewrite-rex-scannerless-glr-parse(parser:parse-json($input))