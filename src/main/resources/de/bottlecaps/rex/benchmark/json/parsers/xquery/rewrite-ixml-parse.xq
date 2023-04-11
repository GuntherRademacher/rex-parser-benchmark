module namespace rip = 'rewrite-ixml-parse';

import module namespace h2i = "hex-to-integer" at "hex-to-integer.xq";

declare function rip:rewrite-ixml-parse($nodes as node()*, $key as attribute(key)?) as node()*
{
  for $node in $nodes
  return
    typeswitch ($node)
    case document-node() return
      document {rip:rewrite-ixml-parse($node/node(), ())}
    case element(unicode) return
      text {codepoints-to-string(h2i:hex-to-integer($node/@code))}
    case element(member) return
      let $key := rip:rewrite-ixml-parse($node/key, ())
      return rip:rewrite-ixml-parse($node/value/*, attribute key {$key})
    case element() return
      element
        {QName('http://www.w3.org/2005/xpath-functions', local-name($node))}
        {rip:rewrite-ixml-parse(($key, $node/@*, $node/node()), ())}
    default return
      $node
};