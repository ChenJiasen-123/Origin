import { Capacitor, registerPlugin } from '@capacitor/core'
const widget = registerPlugin('ScheduleWidget')
export async function widgetLaunchTarget(){if(Capacitor.getPlatform()!=='android')return false;try{return(await widget.launchTarget()).schedule}catch{return false}}
export async function syncWidget(items){if(Capacitor.getPlatform()!=='android')return;try{await widget.update({items})}catch(error){console.warn('Widget sync failed',error)}}
export async function pinWidget(){if(Capacitor.getPlatform()!=='android')return{supported:false,requested:false,count:0};return widget.pin()}
export async function widgetStatus(){if(Capacitor.getPlatform()!=='android')return{supported:false,count:0};return widget.status()}
export async function openWidgetHome(){return widget.openHome()}
export async function syncWidgetLanguage(value){if(Capacitor.getPlatform()==='android')await widget.language({value})}
