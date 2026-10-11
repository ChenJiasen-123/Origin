// Select exact date ranges before constructing the model context; never reject by total course count.
const pad=n=>String(n).padStart(2,'0')
const dateKey=d=>`${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}`
function addDays(d,n){const next=new Date(d);next.setDate(next.getDate()+n);return next}
export function selectAIContext(text, schedules, now=new Date(), history=[]) {
  const input=String(text).toLowerCase(), today=new Date(now.getFullYear(),now.getMonth(),now.getDate())
  let from=null,to=null
  const explicit=input.match(/(20\d{2})\s*(?:年|[-/.])\s*(\d{1,2})\s*(?:月|[-/.])\s*(\d{1,2})\s*(?:日|号)?/)
  if(explicit){const d=new Date(Number(explicit[1]),Number(explicit[2])-1,Number(explicit[3]));if(d.getFullYear()===+explicit[1]&&d.getMonth()===+explicit[2]-1&&d.getDate()===+explicit[3])from=to=dateKey(d)}
  const iso=input.match(/\b(20\d{2}-\d{2}-\d{2})\b/g)||[]
  if(iso.length>=2){from=iso.slice().sort()[0];to=iso.slice().sort().at(-1)}
  if(!from){
    if(/大后天/.test(input))from=to=dateKey(addDays(today,3))
    else if(/后天|day after tomorrow/.test(input))from=to=dateKey(addDays(today,2))
    else if(/明天|tomorrow/.test(input))from=to=dateKey(addDays(today,1))
    else if(/昨天|yesterday/.test(input))from=to=dateKey(addDays(today,-1))
    else if(/今天|今日|today/.test(input)||/^(上午|下午|晚上|早上|中午)的/.test(input))from=to=dateKey(today)
    else if(/本周|这周|下周|上周|this week|next week|last week/.test(input)){const offset=/下周|next week/.test(input)?7:/上周|last week/.test(input)?-7:0;const monday=addDays(today,-((today.getDay()+6)%7)+offset);from=dateKey(monday);to=dateKey(addDays(monday,6))}
    else if(/本月|这个月|下个月|下月|this month|next month/.test(input)){const offset=/下月|下个月|next month/.test(input)?1:0;from=dateKey(new Date(today.getFullYear(),today.getMonth()+offset,1));to=dateKey(new Date(today.getFullYear(),today.getMonth()+offset+1,0))}
  }
  const dates=[...new Set(schedules.map(s=>(s.start_time||'').slice(0,10)))].sort()
  let candidates=schedules
  if(from)candidates=schedules.filter(s=>{const d=(s.start_time||'').slice(0,10);return d>=from&&d<=to})
  else if(schedules.length>180){
    const terms=input.replace(/[，。！？、,.!?]/g,' ').split(/\s+/).filter(x=>x.length>1)
    const named=schedules.filter(s=>s.name && (input.includes(s.name.toLowerCase()) || terms.some(t=>s.name.toLowerCase().includes(t))))
    if(named.length)candidates=named
    else {from=dateKey(addDays(today,-7));to=dateKey(addDays(today,35));candidates=schedules.filter(s=>{const d=(s.start_time||'').slice(0,10);return d>=from&&d<=to})}
  }
  const selected=[],seen=new Set();let chars=0
  for(const s of candidates.slice().sort((a,b)=>String(a.start_time).localeCompare(String(b.start_time)))) {
    const item={id:String(s.id),name:s.name,start_time:s.start_time,end_time:s.end_time,location:s.location||'',note:(s.note||'').slice(0,300),who:s.who||[],is_completed:s.is_completed===true,reminder_time:s.reminder_time||null,color:s.color||'#409EFF'}
    const size=JSON.stringify(item).length
    if(chars+size>65000)break
    selected.push(item);seen.add(String(s.id));chars+=size
  }
  // Preserve previously referenced courses so follow-up edits still resolve original IDs.
  for(const s of schedules){if(seen.has(String(s.id)))continue;const referenced=history.some(m=>typeof m.content==='string'&&m.content.includes(`"id":"${s.id}"`));if(referenced&&selected.length<220){selected.push({...s,id:String(s.id),note:(s.note||'').slice(0,300)});seen.add(String(s.id))}}
  return { schedules:selected, scope:{from,to,total:schedules.length,matched:candidates.length,included:selected.length,truncated:selected.length<candidates.length,availableDates:dates.length ? [dates[0],dates.at(-1)] : []} }
}
