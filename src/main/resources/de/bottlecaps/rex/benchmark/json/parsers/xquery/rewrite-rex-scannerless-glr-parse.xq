module namespace rrsgp = "rewrite-rex-scannerless-glr-parse";

import module namespace h2i = "hex-to-integer" at "hex-to-integer.xq";

declare function rrsgp:rewrite($nodes, $key)
{
  for $node in $nodes
  return
    typeswitch ($node)
  case document-node() return
    rrsgp:rewrite($node/*, ())
  case element(ws) return
    ()
  case element(char) return
    if ($node/escaped) then
        let $char := substring($node, 2, 1)
        return
          if ($char = 'r') then
            text {'&#xD;'}
          else if ($char eq 'n') then
            text {'&#xA;'}
          else if ($char eq 't') then
            text {'&#x9;'}
          else if ($char eq 'u') then
            text {codepoints-to-string(h2i:hex-to-integer($node//code))}
        else
          text {$char}
    else
      text{$node}
    case element(null) return
      element
        {QName('http://www.w3.org/2005/xpath-functions', 'null')}
        {$key}
  case element(string) return
    element
        {QName('http://www.w3.org/2005/xpath-functions', local-name($node))}
        {$key, rrsgp:rewrite($node/_string_list_option/node(), ())}
  case element(number) | element(boolean) return
      element
        {QName('http://www.w3.org/2005/xpath-functions', local-name($node))}
        {$key, text {$node}}
    case element(map) return
      element
        {QName('http://www.w3.org/2005/xpath-functions', 'map')}
        {$key, rrsgp:rewrite($node/_map_list_option/node(), ())}
    case element(_map_list) return
      rrsgp:rewrite($node/(_map_list, member), ())
    case element(array) return
      element
        {QName('http://www.w3.org/2005/xpath-functions', 'array')}
        {$key, rrsgp:rewrite($node/_array_list_option/node(), ())}
    case element(_array_list) return
      rrsgp:rewrite($node/(_array_list, value), ())
    case element(member) return
      let $key := rrsgp:rewrite($node/key/string, ())/string(.)
      return rrsgp:rewrite($node/value, attribute key {$key})
  case element() return
    let $name := local-name($node)
    return
      if (ends-with($name, "_option") or
          ends-with($name, "_list") or
          ends-with($name, "_choice") or
          $name = ("TOKEN", "value", "json")) then
      rrsgp:rewrite($node/node(), $key)
    else
      element {node-name($node)} {rrsgp:rewrite($node/node(), ())}
  default return
    $node
};

declare function rrsgp:rewrite-rex-scannerless-glr-parse($parse-tree as element()) as document-node()
{
  if (empty($parse-tree/self::ERROR)) then
    document {rrsgp:rewrite($parse-tree, ())}
  else
    error(xs:QName("rrsgp:rewrite-rex-scannerless-glr-parse"), concat("&#10;    ", replace($parse-tree, "&#10;", "&#10;    ")))
};