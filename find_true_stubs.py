import glob
import re

stubs = []
for file in glob.glob('src/main/java/vltno/essentials/commands/*.java'):
    with open(file, 'r', encoding='utf-8') as f:
        content = f.read()
        
    # Find all execute methods
    methods = re.findall(r'public static int execute[a-zA-Z0-9_]*\([^)]*\)\s*(?:throws[^{]+)?\{(.*?)\}', content, re.DOTALL)
    
    is_stub = True
    for m in methods:
        m_clean = m.strip()
        lines = [l.strip() for l in m_clean.split('\n') if l.strip()]
        
        # If any execute method has actual logic (more than 3 lines), it's probably implemented
        if len(lines) > 3:
            is_stub = False
            break
            
        # If it doesn't say "Usage:" and doesn't say "not implemented" but returns 1, it's likely a simple implemented command (like /ping)
        if 'Usage:' not in m and 'not implemented' not in m and 'not supported' not in m and 'skipping for now' not in m:
            is_stub = False
            break
            
    if is_stub and methods:
        stubs.append(file)

for stub in stubs:
    print(f"--- {stub} ---")
    with open(stub, 'r', encoding='utf-8') as f:
        print(f.read())
    print("\n")
