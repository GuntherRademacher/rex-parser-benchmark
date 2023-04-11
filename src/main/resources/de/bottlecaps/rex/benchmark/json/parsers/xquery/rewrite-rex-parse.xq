module namespace rrp = "rewrite-rex-parse";

import module namespace h2i = "hex-to-integer" at "hex-to-integer.xq";

declare function rrp:rewrite-rex-parse($nodes as node()*, $key as attribute(key)?) as node()*
{
  for $node in $nodes
  return
    typeswitch ($node)
    case document-node() return
      document {rrp:rewrite-rex-parse($node/*, ())}
    case element(escaped) return
      let $char := substring($node, 2, 1)
      return
        if ($char = 'r') then
          text {'&#xD;'}
        else if ($char eq 'n') then
          text {'&#xA;'}
        else if ($char eq 't') then
          text {'&#x9;'}
        else if ($char eq 'u') then
          text {codepoints-to-string(h2i:hex-to-integer($node/hex4))}
        else
          text {$char}
    case element(unescaped) return
      $node/text()
    case element(null) return
      element
        {QName('http://www.w3.org/2005/xpath-functions', 'null')}
        {$key}
    case element(number) | element(boolean) return
      element
        {QName('http://www.w3.org/2005/xpath-functions', local-name($node))}
        {$key, text {$node}}
    case element(string) return
      element
        {QName('http://www.w3.org/2005/xpath-functions', 'string')}
        {$key, rrp:rewrite-rex-parse($node/*, ())}
    case element(object) return
      element
        {QName('http://www.w3.org/2005/xpath-functions', 'map')}
        {$key, rrp:rewrite-rex-parse($node/*, ())}
    case element(array) return
      element
        {QName('http://www.w3.org/2005/xpath-functions', 'array')}
        {$key, rrp:rewrite-rex-parse($node/*, ())}
    case element(member) return
      let $key := rrp:rewrite-rex-parse($node/string, ())
      return rrp:rewrite-rex-parse($node/value, attribute key {$key})
    case element() return
      rrp:rewrite-rex-parse($node/*, $key)
    case element(TOKEN) return
      ()
    default return
      $node
};

declare function rrp:rewrite-rex-parse($parse-tree as element()) as document-node()
{
  if (empty($parse-tree/self::ERROR)) then
    document {rrp:rewrite-rex-parse($parse-tree, ())}
  else
    error(xs:QName("rrp:parse"), concat("&#10;    ", replace($parse-tree, "&#10;", "&#10;    ")))
};