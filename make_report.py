import csv, math, statistics
from collections import defaultdict
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
from docx import Document
from docx.shared import Inches, Pt
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import OxmlElement
from docx.oxml.ns import qn

rows=list(csv.DictReader(open('results/results.csv', newline='')))
groups=defaultdict(list)
for r in rows: groups[(r['family'],r['algorithm'],r['input'],int(r['n']))].append(r)
def load_font(size, bold=False):
    for name in (('C:/Windows/Fonts/arialbd.ttf', 'DejaVuSans-Bold.ttf') if bold else ('C:/Windows/Fonts/arial.ttf', 'DejaVuSans.ttf')):
        try: return ImageFont.truetype(name, size)
        except OSError: pass
    return ImageFont.load_default(size=size)
font=load_font(22)
small=load_font(18)
bold=load_font(25, True)
def measure(family, alg, typ, n, metric):
    return statistics.median(float(r[metric]) for r in groups[(family,alg,typ,n)])
colors=['#2459a6','#c34335','#23845e','#9a5aaa']
def chart(filename,title,ylabel,series,log=False):
    img=Image.new('RGB',(1200,660),'white'); d=ImageDraw.Draw(img)
    left,top,right,bottom=140,85,1140,500
    d.text((left,18),title,font=bold,fill='black')
    points=[(n, v) for _, values in series for n,v in values]
    xs=sorted(set(n for n,v in points)); ymax=max(v for n,v in points)
    ymax=math.ceil(math.log10(ymax)) if log else ymax*1.08
    ymin=0
    for i in range(6):
        yy=bottom-(bottom-top)*i/5
        d.line((left,yy,right,yy),fill='#dddddd',width=1)
        v=10**(ymax*i/5) if log else ymax*i/5
        label=(f'{v:.0e}' if log else f'{v:,.2f}' if ymax<10 else f'{v:,.0f}')
        box=d.textbbox((0,0),label,font=small); d.text((left-12-(box[2]-box[0]),yy-10),label,font=small,fill='black')
    d.line((left,top,left,bottom,right,bottom),fill='black',width=2)
    for j,n in enumerate(xs):
        x=left+(right-left)*(n-xs[0])/(xs[-1]-xs[0]); label=f'{n:,}'; box=d.textbbox((0,0),label,font=small); d.text((x-(box[2]-box[0])/2,bottom+10),label,font=small,fill='black')
    d.text((470,535),'Input length n (characters)',font=font,fill='black')
    # Horizontal axis caption keeps labels readable in Word.
    d.text((left,57),ylabel+(' (log scale)' if log else ''),font=small,fill='black')
    for ix,(label,values) in enumerate(series):
        coords=[]
        for n,v in values:
            x=left+(right-left)*(n-xs[0])/(xs[-1]-xs[0])
            y=bottom-(bottom-top)*((math.log10(max(v,1))/ymax) if log else v/ymax)
            coords.append((x,y))
        d.line(coords,fill=colors[ix],width=4)
        for x,y in coords: d.ellipse((x-5,y-5,x+5,y+5),fill=colors[ix])
        x=left+(ix%2)*480; y=580+(ix//2)*32
        d.line((x,y+10,x+35,y+10),fill=colors[ix],width=4); d.text((x+45,y),label,font=small,fill='black')
    img.save('results/plots/'+filename)
def data(family,alg,typ,metric):
    ns=sorted({k[3] for k in groups if k[:3]==(family,alg,typ)})
    return [(n,statistics.median(float(x[metric]) for x in groups[(family,alg,typ,n)])) for n in ns]
chart('edit_work.png','Edit distance work by input','Calls or cells (count)',[(a.replace('_',' ')+' / '+t,data('edit',a,t,'work')) for a,t in [('recursive','disjoint'),('recursive','identical'),('table','disjoint'),('two_rows','disjoint')]],True)
chart('search_work.png','Search comparisons by input','Character comparisons (count)',[(a+' / '+t,data('search',a,t,'work')) for a,t in [('naive','adversarial'),('kmp','adversarial'),('naive','random'),('kmp','random')]])
chart('search_time.png','Search elapsed time','Median elapsed time (ms)',[(a+' / '+t,data('search',a,t,'time_ms')) for a,t in [('naive','adversarial'),('kmp','adversarial'),('naive','random'),('kmp','random')]])

doc=Document(); sec=doc.sections[0]; sec.top_margin=sec.bottom_margin=Inches(.65); sec.left_margin=sec.right_margin=Inches(.75)
styles=doc.styles; styles['Normal'].font.name='Calibri'; styles['Normal'].font.size=Pt(10)
styles['Normal'].paragraph_format.space_after=Pt(4)
styles['Title'].font.name='Calibri'; styles['Title'].font.size=Pt(18); styles['Title'].font.color.rgb=None
for style in styles:
    for border in style.element.findall('.//' + qn('w:pBdr')):
        border.getparent().remove(border)
footer=sec.footer.paragraphs[0]; footer.alignment=WD_ALIGN_PARAGRAPH.CENTER
field=OxmlElement('w:fldSimple'); field.set(qn('w:instr'), 'PAGE'); footer._p.append(field)
markdown=['# Assignment 4 Dynamic Programming and String Algorithms']
def para(text, boldlead=None):
    markdown.append(text)
    p=doc.add_paragraph()
    if boldlead and text.startswith(boldlead): p.add_run(boldlead).bold=True; p.add_run(text[len(boldlead):])
    else: p.add_run(text)
    return p
def heading(s):
    doc.add_heading(s,level=1)
    markdown.append('## '+s)
def figure(name,caption):
    markdown.append('!['+caption+'](results/plots/'+name+')')
    doc.add_picture('results/plots/'+name,width=Inches(6.8)); doc.paragraphs[-1].alignment=WD_ALIGN_PARAGRAPH.CENTER
    p=para(caption); p.runs[0].italic=True
doc.add_paragraph('Assignment 4 Dynamic Programming and String Algorithms',style='Title')
para('Pashanov Yelnur | SE2523 | Design and Analysis of Algorithms')
heading('Experiment Design Card')
para('Questions. How does edit-distance work grow for recursion and dynamic programming? How does pattern structure affect naive search and KMP?', 'Questions.')
para('Sizes. Edit distance: n = 2, 4, 8, 10 for equal-length strings; this fivefold range shows branching while keeping recursion practical. Search: text length n = 2,048, 4,096, 8,192, 16,384 with a 32-character pattern. Doubling the text separates growth from fixed overhead; the pattern is long enough to expose repeated comparisons and is held constant for a fair comparison.', 'Sizes.')
para('Inputs. The generator uses seed 20261108. Edit distance uses random ACGT strings, identical strings, and disjoint A...A versus B...B strings, respectively showing typical behavior, the matching shortcut, and maximum branching. Search uses random ACGT strings and an adversarial text A...A with pattern A repeated 31 times followed by B; the latter forces repeated naive comparisons.', 'Inputs.')
para('Metrics. Recursive calls reveal repeated subproblems; DP cell updates reveal table work; character comparisons reveal repeated checking in both search phases. All counters are incremented inside the algorithms. Calls and cells are different units, so their ratio is not a speedup. Time includes allocation and preprocessing but excludes input generation and CSV writing; five runs follow three warm-ups and the middle sorted time is reported.', 'Metrics.')
para('Predictions. Recursive work should grow much faster on disjoint strings than the quadratic DP tables, while identical strings follow one branch. The adversarial search should cause roughly 32 comparisons per candidate for naive search; KMP should remain linear.', 'Predictions.')
para('Plots. Edit work on a logarithmic axis compares growth; search comparisons reveal input sensitivity; median search time shows the observed timing. After exploratory runs, disjoint edit strings were added to expose worst-case recursion. Before the final rerun, edit sizes were changed from 4, 6, 8, 10 to 2, 4, 8, 10 to meet the fourfold-range rule.', 'Plots.')
doc.add_page_break()
heading('Plots and Results')
figure('edit_work.png','The disjoint recursive case grows sharply, while both DP implementations perform n² cell updates and overlap on the graph.')
figure('search_work.png','At n = 16,384, naive search makes 523,296 comparisons on the adversarial input versus 32,798 for KMP.')
doc.add_page_break()
figure('search_time.png','The adversarial naive search has the highest median at n = 16,384, but short-run timings fluctuate.')
heading('Prediction versus Result')
para(f'Edit distance. The result supports the prediction: at n = 10, disjoint recursion made 12,146,179 calls, versus 100 cell updates for either DP method and 11 calls for identical recursion. Random recursion made {measure("edit","recursive","random",10,"work"):,.0f} calls at that size. Thus input structure, as well as length, determines recursive work.')
para(f'String search. At n = 16,384, adversarial naive search made 523,296 comparisons and KMP made 32,798, including LPS construction. On random input they made {measure("search","naive","random",16384,"work"):,.0f} and {measure("search","kmp","random",16384,"work"):,.0f} comparisons, respectively. This agrees with the predicted adversarial advantage, although a fixed pattern length makes both curves linear in text length.')
heading('Short Answers')
para('Which input made search slowest? The adversarial text of repeated A and pattern ending in B produced the largest search time and comparison count for naive search at the largest size. Naive search checks 31 matching characters before failing at each candidate. KMP reuses the LPS values and does not move backward in the text.')
para(f'How large could recursion handle? The largest measured equal-length input was n = 10, including the disjoint worst case. It took a median of about {measure("edit","recursive","disjoint",10,"time_ms"):.3f} ms and 12,146,179 calls. Larger sizes were not measured; repeated branching, rather than a proven hard limit, motivated the chosen bound.')
doc.add_page_break()
heading('Short Answers Continued')
para('What do two rows gain and lose? They reduce extra storage from O(nm) to O(m) while preserving the distance and O(nm) time. The full table is no longer available for directly tracing an edit sequence. Recovering that sequence would require extra work or another method.')
para('Which search for the city platform? KMP is a sound default for long logs or repetitive text because its O(n + m) worst-case work is predictable and it finds overlapping matches. For short typical queries, naive search is simpler and may be competitive. The workload and fixed 32-character patterns here do not prove KMP is always faster.')
heading('Time Measurements')
para(f'On Windows with Microsoft OpenJDK 17.0.20.1, the n = 10 disjoint edit medians were {measure("edit","recursive","disjoint",10,"time_ms"):.4f} ms (recursive), {measure("edit","table","disjoint",10,"time_ms"):.4f} ms (table) and {measure("edit","two_rows","disjoint",10,"time_ms"):.4f} ms (two rows). At n = 16,384, search medians were {measure("search","naive","adversarial",16384,"time_ms"):.3f} ms (adversarial naive) and {measure("search","kmp","adversarial",16384,"time_ms"):.3f} ms (adversarial KMP); random cases were {measure("search","naive","random",16384,"time_ms"):.3f} ms and {measure("search","kmp","random",16384,"time_ms"):.3f} ms. Timings are not monotonic across all sizes, and tiny DP cases cannot establish a stable speed ranking. JVM compilation, scheduling and timer noise can outweigh small workloads; three warm-ups and five runs do not eliminate them. The counters provide repeatable evidence of work growth; reruns reproduce inputs and counts, not exact elapsed times.')
heading('Correctness')
para('Eight JUnit 5 tests check all reference values, empty inputs and patterns, overlaps, counters and 100 fixed-seed random cross-checks per family. Another 1,000 generated search cases compare each implementation with Java String.indexOf as an independent reference. Both families expose a common two-argument interface plus an instrumented overload.')
doc.save('DAA_Assignment4_Pashanov_Yelnur.docx')
Path('REPORT.md').write_text('\n\n'.join(markdown)+'\n', encoding='utf-8')
