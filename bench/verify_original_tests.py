"""Fail if any original test method or assertion was modified."""
import pathlib,re,subprocess,json
path='src/test/java/com/flashsale/engine/OrderFlowIntegrationTests.java'
original=subprocess.check_output(['git','show','c6163ac8c68a24ca246c0a30a982d9e183b7f9a3:'+path],text=True)
current=pathlib.Path(path).read_text()
def methods(s):
 result={}
 for match in re.finditer(r'    @Test\s+@Order\(\d+\)\s+void (\w+)\([^)]*\)[^{]*\{',s):
  depth=1;pos=match.end()
  while depth:
   if s[pos]=='{':depth+=1
   elif s[pos]=='}':depth-=1
   pos+=1
  result[match[1]]=s[match.start():pos]
 return result
orig=methods(original);now=methods(current)
for name,body in orig.items():assert now.get(name)==body, f'Original test modified: {name}'
print(json.dumps({'original_methods_unchanged':len(orig),'current_methods':len(now)}))
