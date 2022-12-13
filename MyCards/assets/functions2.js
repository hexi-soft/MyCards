// my javascript function library

var g_way_to_read = 'tts'
var g_tts_start = false

function check_if_tts_ok(msg){
if (!g_tts_start){
g_way_to_read = 'audio'
speak_msg(msg,null)
}else{
g_way_to_read = 'tts'
}
}

function on_tts_start(){
g_tts_start = true
}

function speak_msg(msg, onstart, onend, rate, lang, v_uri){
if (g_way_to_read=='tts'){
Speak(msg,on_tts_start, onend, rate?rate:1,lang?lang:'en_US',v_uri)
setTimeout("check_if_tts_ok('"+msg+"')", 100)
}else if(g_way_to_read == 'audio'){
play_sentence_audio(msg,onend,onstart,lang,rate,v_uri)
}else{
Speak(msg,onstart,onend,rate?rate:1,lang?lang:'en_US', v_uri)
}
}

var DOMextend=function(o,name,fn){
 eval(o+'.prototype.'+name+"=fn");
};
DOMextend ('HTMLCollection', 'filter',
function(f){
var a = []
for (var i=0; i<this.length; ++i){
if (f(this[i]))
 a.push(this[i])
}
return a
}
);

function StringBuilder() {  
this._stringArray = new Array();  
}             
StringBuilder.prototype.append = function(str){  
this._stringArray.push(str);  
}  
StringBuilder.prototype.toString = function(joinGap){
if (joinGap)
return this._stringArray.join(joinGap); 
else return this._stringArray.join('');
} 

function createRectLinearGradient(c, x, y, w, h){
 return c.createLinearGradient(x, y, x+w, y+h);
}

function $2(node){
document.body.appendChild(node)
}

function insertAfter(newElem, targetElem){
var parent = targetElem.parentNode
if (parent.lastChild == targetElem){
parent.appendChild(newElem)
}else{
parent.insertBefore(newElem, targetElem.nextSibling)
}
}

function addLoadEvent(func){
var oldonload = window.onload
if (typeof window.onload != 'function'){
window.onload = func
}else{
window.onload = function(){
oldonload()
func()
}
}
}

var sUserAgent = navigator.userAgent
var sAppName = navigator.appName

//两个兼容IE不支持pageX和pageY属性的函数
function pointerX(event) {
   var docElement = document.documentElement,

   body = document.body || { scrollLeft: 0 };
    return event.pageX || (event.clientX +
     (docElement.scrollLeft || body.scrollLeft) -
      (docElement.clientLeft || 0));
}
function pointerY(event) {

  	var docElement = document.documentElement,

     body = document.body || { scrollTop: 0 };

    return  event.pageY || (event.clientY +

       (docElement.scrollTop || body.scrollTop) -

       (docElement.clientTop || 0));

}

function is_Chinese(code)
{
if (code>=0x4E00 && code<=0x9FA5)
  return true;
return false;
}

function $1(elem)
{
  return document.createElement(elem)
}

function $3(text)
{
  return document.createTextNode(text)
}

function read_file (filepath)
{
var s = ''
var fin = new ActiveXObject("Scripting.FileSystemObject")
if (fin == null)
{
 alert("I'm sorry but I could not create ActiveXObjec, hence I could not open your file: "+filepath)
 return s
}
var file = fin.OpenTextFile(filepath,1)
if (file == null)
{
 alert("I'm sorry but I could not open your file: "+filepath)
 return s
}
s = file.ReadAll()
return s
}

function create_xml_http() 
{
var xhr = false;
if (window.XMLHttpRequest) 
    xhr = new XMLHttpRequest();
else
{
   if (window.ActiveXObject)
     try {
        xhr = new ActiveXObject("Microsoft.XMLHTTP");
    }
    catch (e) {}
}
if (!xhr)
  alert("XMLHttpRequest could not be created.");
return xhr;
}

var xhr = false;
var ok_callback=null
function init_xhr(){
   xhr = create_xml_http();
   xhr.onreadystatechange = xhr_callback;
}

function xhr_callback()
{
  if (xhr.readyState == 4)
  {
    if (xhr.status == 200)
    {
       if (ok_callback != null)
       {
          ok_callback(xhr)
          ok_callback = null
       }
    }
    else
    {
     play_audio("/waves/dell.wav");
     //alert ('HTTP response interior error: '+xhr.statusText);
    }
  }
}

function xhr_get(url, params, callback)
{
  if (xhr)
  {
    //xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
   if (params != null && params.length>0)
      url = url+'?'+encodeURI(params)
    xhr.open('GET', url, true);
    if (typeof callback != 'undefined')
      ok_callback = callback;
    xhr.send(null);
  }
}

function xhr_post(url, post_data, callback)
{
  if (xhr)
  {
    //xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.open('POST', url, true);
    if (typeof callback != 'undefined')
      ok_callback = callback;
    //post_data = encodeURIComponent(post_data);
    post_data = encodeURI(post_data);
    xhr.send(post_data);
  }
}

function http_request(url, post_data, method, callback)
{
   xhr.onreadystatechange = callback;
   //xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
   xhr.open(method, url, true);
   xhr.send(post_data);
}

function play_audio (audioURL) {
if (sUserAgent.indexOf('MSIE')>0)
{
  var audioElement = $0('bgsoundId');
  if (audioElement==null)
  {
     audioElement = $1('bgsound')
     audioElement.setAttribute('id', 'bgsoundId')
     document.body.appendChild(audioElement)
  }
  audioElement.src = audioURL; 
  audioElement.loop = "1"; 
  return true
}
else{
  var audioElement = document.createElement('audio'); 
  audioElement.setAttribute('src', audioURL); 
  audioElement.load; 
  audioElement.play();
}
}

var VoiceObj=null;

function Stop()
{
 if (window.speechSynthesis)
   window.speechSynthesis.cancel();
 else{
  VoiceObj.Speak( "", 2 );
 }
}

function Resume()
{
 if (window.speechSynthesis)
   window.speechSynthesis.resume();
}

function Pause()
{
 if (window.speechSynthesis)
   window.speechSynthesis.pause();
 else{
  VoiceObj.Speak( "", 2 );
 }
}

function okMsg()
{
    Speak('ok');
}

function TextMsg()
{
   Speak(this.innerText);
}


function HashTable() 
{ 
var size = 0; 
var entry = new Object(); 
this.add = function (key , value) 
{ 
if(!this.containsKey(key)) 
{ 
  size ++ ; 
  entry[key]=value;
} else entry[key] = entry[key]+value; 
} 
this.getValue = function (key) 
{ 
return this.containsKey(key) ? entry[key] : 0; 
} 
this.remove = function ( key ) 
{ 
if( this.containsKey(key) && ( delete entry[key] ) ) 
{ 
size --; 
} 
} 
this.containsKey = function ( key ) 
{ 
return (key in entry); 
} 
this.containsValue = function ( value ) 
{ 
for(var prop in entry) 
{ 
if(entry[prop] == value) 
{ 
return true; 
} 
} 
return false; 
} 
this.getValues = function () 
{ 
var values = new Array(); 
for(var prop in entry) 
{ 
values.push(entry[prop]); 
} 
return values; 
} 
this.getKeys = function () 
{ 
var keys = new Array(); 
for(var prop in entry) 
{ 
keys.push(prop); 
} 
return keys; 
} 
this.getSize = function () 
{ 
return size; 
} 
this.clear = function () 
{ 
size = 0; 
entry = new Object(); 
} 
}

function toggleMenu() {
 var startMenu = this.href.lastIndexOf("/")+1;
 var stopMenu = this.href.lastIndexOf(".");
 var thisMenuName = this.href.substring(startMenu,stopMenu);

 var thisMenu = document.getElementById(thisMenuName).style;
 if (thisMenu.display == "block") {	
 	thisMenu.display = "none";
 }
 else {
  thisMenu.display = "block";
 }
 return false;
}

function init_tts() {
if (window.ActiveXObject != undefined){
   VoiceObj = new ActiveXObject("Sapi.SpVoice")
}
}

function cleanup_tts()
{
  delete VoiceObj;
}

function init_menu_links ()
{
 var allLinks = document.getElementsByTagName("a");
 for (var i=0; i<allLinks.length; i++) {
   if (allLinks[i].className.indexOf("menuLink") > -1) {
   allLinks[i].onclick = toggleMenu;
  }
 }
}

function clearText(field){
    if (field.defaultValue == field.value) field.value = '';
    else if (field.value == '') field.value = field.defaultValue;
}

var xml_document = document
function x$(id)
{
  return xml_document.getElementById(id);
}

function x$$(tag)
{
  return xml_document.getElementsByTagName(tag);
}

function x$1(elem)
{
  return xml_document.createElement(elem)
}

function x$3(text)
{
  return xml_document.createTextNode(text)
}

function $0(id)
{
  return document.getElementById(id);
}

function $1(elem)
{
  return document.createElement(elem)
}

function $3(text)
{
  return document.createTextNode(text)
}

function $2(node){
document.body.appendChild(node)
}

function $$(tag)
{
  return document.getElementsByTagName(tag);
}

// 两个频率排序比较函数
function FreqAscSort(a, b)
{
    return a.freq-b.freq;
}
function FreqDescSort(a, b)
{
    return b.freq-a.freq;
}

// 固定宽度数字输出
function fix(num, length) {
  return ('' + num).length < length ? ((new Array(length + 1)).join('0') + num).slice(-length) : '' + num;
}
/*
function Speak(s, start_callback,end_callback, rate, lang, voiceURI) {
 try
 { 
  if(VoiceObj){
   Stop()
   VoiceObj.Speak (s, 1)
  }else{
   if (window.SpeechSynthesisUtterance){
    var utterTTS = new window.SpeechSynthesisUtterance(s)
    if (start_callback)utterTTS.onstart=start_callback
    if (end_callback)utterTTS.onend=end_callback
    if(rate)utterTTS.rate = rate
    if(lang)utterTTS.lang = lang
    else {
utterTTS.lang = 'en_US';
}
if(voiceURI)utterTTS.voiceURI = voiceURI
else utterTTS.voiceURI = 'English United States'
    speechSynthesis.speak(utterTTS)
//debug('speak: '+s)
   }else{
    debug("Your browser doesn't support speechSynthesis.")
   }
  }
 }
 catch(e)
 {
   alert('Fail to speak:'+ e.message)
 }
}
*/

function speak_this(e)
{
 Speak(e.innerText)
}

var g_debug = true
function debug(msg){
if (g_debug){
var log = document.getElementById('debuglog')
if (!log){
log = document.createElement('div')
log.id = 'debuglog'
log.innerHTML = '<h2>Debug Log</h2>'
document.body.appendChild(log)
}//end if(!log)
var pre = document.createElement('pre')
var text = document.createTextNode(msg)
pre.appendChild(text)
log.appendChild(pre)
}//end if (g_debug)
}//end debug

function debug_text(msg){
if (g_debug){
var log = document.getElementById('debuglog')
if (!log){
log = document.createElement('div')
log.id = 'debuglog'
log.innerHTML = '<h2>Debug Log</h2>'
document.body.appendChild(log)
}//end if(!log)
var text = document.createTextNode(msg)
log.appendChild(text)
}//end if (g_debug)
}//end debug_text

function debug_html(html_msg){
if (g_debug){
var log = document.getElementById('debuglog')
if (!log){
log = document.createElement('div')
log.id = 'debuglog'
log.innerHTML = '<h2>Debug Log</h2>'
document.body.appendChild(log)
}//end if(!log)
var div = document.createElement('div')
div.innerHTML = html_msg
log.appendChild(div)
}//end if (g_debug)
}//end debug_html

function debug_i(msg){
if (g_debug){
var log = document.getElementById('debuglog')
if (!log){
log = document.createElement('div')
log.id = 'debuglog'
log.innerHTML = '<h2>Debug Log</h2>'
document.body.appendChild(log)
}//end if(!log)

var debug_content = document.getElementById('debug_content')
if (debug_content===null){
debug_content = document.createElement('div')
debug_content.id = 'debug_content'
document.body.appendChild(debug_content)
}
debug_content.innerHTML = msg
}//end if (g_debug)
}//end debug_i

function getCookie(cookieName) {
var cookieString = document.cookie;
var start = cookieString.indexOf(cookieName + '=');
if (start == -1)
return null;
start += cookieName.length + 1;
var end = cookieString.indexOf(';', start);
if (end == -1) return unescape(cookieString.substring(start));
return unescape(cookieString.substring(start, end));
}

function d2weekday(n){
var ret = ''
switch(n){
case 0: ret = 'Sunday'; break
case 1: ret = 'Monday'; break
case 2: ret = 'Tuesday'; break
case 3: ret = 'Wednesday'; break
case 4: ret = 'Thursday'; break
case 5: ret = 'Friday'; break
case 6: ret = 'Saturday'; break
default: ret = 'invalid day'
}
return ret
}

function get_time(){
var d = new Date()
var year = d.getFullYear()
var month = d.getMonth()
var date = d.getDate()
var day = d.getDay()
var hours = d.getHours()
var minutes = d.getMinutes()
var seconds = d.getSeconds()
return {
  year:year,month:month+1,date:date,day:day,
  hours:hours,minutes:minutes,seconds:seconds
 }
}

function language(c){
if (c>0 && c<256||c==8217||c==8221||c==8220){
return 'en_US'
}else{
return 'zh_CN_#Hans'
//return 'zh-CN'
}
}

function Speak(s, start_callback,end_callback, rate, lang, voiceURI) {
if (myAndroid){
myAndroid.speak(s,rate,end_callback,true);
}else{
 try
 { 
   if (window.SpeechSynthesisUtterance){
    var utterTTS = new window.SpeechSynthesisUtterance(s)
    if (start_callback)utterTTS.onstart=start_callback
    if (end_callback)utterTTS.onend=end_callback
    if(rate)utterTTS.rate = rate
    if(lang)utterTTS.lang = lang
    else {
utterTTS.lang = 'en_US';
}
if(voiceURI)utterTTS.voiceURI = voiceURI
else utterTTS.voiceURI = 'English United States'
    speechSynthesis.speak(utterTTS)
//debug('speak: '+s)
   }else{
    debug("Your browser doesn't support speechSynthesis.")
   }
 }
 catch(e)
 {
   alert('Fail to speak:'+ e.message)
 }
}
}

function gtts(s, rate, onend){
//debug('gtts begins...s='+s+'{'+s.length+'}')
if (s.length>0){
var l = language(s.charCodeAt(0))
var start = 0
for (var i=1; i<s.length; ++i){
var lang = language(s.charCodeAt(i))
if (lang != l){
var ss = s.substring(start, i)
//debug ('ss ='ss)
if (ss.trim()){
//debug('gtts: '+ss)
Speak(ss,null,null,rate?rate:1,l)
}
start = i
l = lang
}//if(lang!=l) end
}//for end
ss = s.substring(start)
//debug ('ss ='ss)
if (ss.trim()){
debug('gtts: '+ss)
Speak(ss,null,onend,rate?rate:1,l)
}
}else{ //s.length=0
if(onend){
onend()
}
}
}//function t(s) end


function getQueryString(name) {  

        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");  

        var r = window.location.search.substr(1).match(reg);  

        if (r != null) return unescape(r[2]);  

        return null;  

} 

var MD5 = function (string) {
  
    function RotateLeft(lValue, iShiftBits) {
        return (lValue<<iShiftBits) | (lValue>>>(32-iShiftBits));
    }
  
    function AddUnsigned(lX,lY) {
        var lX4,lY4,lX8,lY8,lResult;
        lX8 = (lX & 0x80000000);
        lY8 = (lY & 0x80000000);
        lX4 = (lX & 0x40000000);
        lY4 = (lY & 0x40000000);
        lResult = (lX & 0x3FFFFFFF)+(lY & 0x3FFFFFFF);
        if (lX4 & lY4) {
            return (lResult ^ 0x80000000 ^ lX8 ^ lY8);
        }
        if (lX4 | lY4) {
            if (lResult & 0x40000000) {
                return (lResult ^ 0xC0000000 ^ lX8 ^ lY8);
            } else {
                return (lResult ^ 0x40000000 ^ lX8 ^ lY8);
            }
        } else {
            return (lResult ^ lX8 ^ lY8);
        }
    }
  
    function F(x,y,z) { return (x & y) | ((~x) & z); }
    function G(x,y,z) { return (x & z) | (y & (~z)); }
    function H(x,y,z) { return (x ^ y ^ z); }
    function I(x,y,z) { return (y ^ (x | (~z))); }
  
    function FF(a,b,c,d,x,s,ac) {
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(F(b, c, d), x), ac));
        return AddUnsigned(RotateLeft(a, s), b);
    };
  
    function GG(a,b,c,d,x,s,ac) {
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(G(b, c, d), x), ac));
        return AddUnsigned(RotateLeft(a, s), b);
    };
  
    function HH(a,b,c,d,x,s,ac) {
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(H(b, c, d), x), ac));
        return AddUnsigned(RotateLeft(a, s), b);
    };
  
    function II(a,b,c,d,x,s,ac) {
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(I(b, c, d), x), ac));
        return AddUnsigned(RotateLeft(a, s), b);
    };
  
    function ConvertToWordArray(string) {
        var lWordCount;
        var lMessageLength = string.length;
        var lNumberOfWords_temp1=lMessageLength + 8;
        var lNumberOfWords_temp2=(lNumberOfWords_temp1-(lNumberOfWords_temp1 % 64))/64;
        var lNumberOfWords = (lNumberOfWords_temp2+1)*16;
        var lWordArray=Array(lNumberOfWords-1);
        var lBytePosition = 0;
        var lByteCount = 0;
        while ( lByteCount < lMessageLength ) {
            lWordCount = (lByteCount-(lByteCount % 4))/4;
            lBytePosition = (lByteCount % 4)*8;
            lWordArray[lWordCount] = (lWordArray[lWordCount] | (string.charCodeAt(lByteCount)<<lBytePosition));
            lByteCount++;
        }
        lWordCount = (lByteCount-(lByteCount % 4))/4;
        lBytePosition = (lByteCount % 4)*8;
        lWordArray[lWordCount] = lWordArray[lWordCount] | (0x80<<lBytePosition);
        lWordArray[lNumberOfWords-2] = lMessageLength<<3;
        lWordArray[lNumberOfWords-1] = lMessageLength>>>29;
        return lWordArray;
    };
  
    function WordToHex(lValue) {
        var WordToHexValue="",WordToHexValue_temp="",lByte,lCount;
        for (lCount = 0;lCount<=3;lCount++) {
            lByte = (lValue>>>(lCount*8)) & 255;
            WordToHexValue_temp = "0" + lByte.toString(16);
            WordToHexValue = WordToHexValue + WordToHexValue_temp.substr(WordToHexValue_temp.length-2,2);
        }
        return WordToHexValue;
    };
  
    function Utf8Encode(string) {
        string = string.replace(/\r\n/g,"\n");
        var utftext = "";
  
        for (var n = 0; n < string.length; n++) {
  
            var c = string.charCodeAt(n);
  
            if (c < 128) {
                utftext += String.fromCharCode(c);
            }
            else if((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            }
            else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }
  
        }
  
        return utftext;
    };
  
    var x=Array();
    var k,AA,BB,CC,DD,a,b,c,d;
    var S11=7, S12=12, S13=17, S14=22;
    var S21=5, S22=9 , S23=14, S24=20;
    var S31=4, S32=11, S33=16, S34=23;
    var S41=6, S42=10, S43=15, S44=21;
  
    string = Utf8Encode(string);
  
    x = ConvertToWordArray(string);
  
    a = 0x67452301; b = 0xEFCDAB89; c = 0x98BADCFE; d = 0x10325476;
  
    for (k=0;k<x.length;k+=16) {
        AA=a; BB=b; CC=c; DD=d;
        a=FF(a,b,c,d,x[k+0], S11,0xD76AA478);
        d=FF(d,a,b,c,x[k+1], S12,0xE8C7B756);
        c=FF(c,d,a,b,x[k+2], S13,0x242070DB);
        b=FF(b,c,d,a,x[k+3], S14,0xC1BDCEEE);
        a=FF(a,b,c,d,x[k+4], S11,0xF57C0FAF);
        d=FF(d,a,b,c,x[k+5], S12,0x4787C62A);
        c=FF(c,d,a,b,x[k+6], S13,0xA8304613);
        b=FF(b,c,d,a,x[k+7], S14,0xFD469501);
        a=FF(a,b,c,d,x[k+8], S11,0x698098D8);
        d=FF(d,a,b,c,x[k+9], S12,0x8B44F7AF);
        c=FF(c,d,a,b,x[k+10],S13,0xFFFF5BB1);
        b=FF(b,c,d,a,x[k+11],S14,0x895CD7BE);
        a=FF(a,b,c,d,x[k+12],S11,0x6B901122);
        d=FF(d,a,b,c,x[k+13],S12,0xFD987193);
        c=FF(c,d,a,b,x[k+14],S13,0xA679438E);
        b=FF(b,c,d,a,x[k+15],S14,0x49B40821);
        a=GG(a,b,c,d,x[k+1], S21,0xF61E2562);
        d=GG(d,a,b,c,x[k+6], S22,0xC040B340);
        c=GG(c,d,a,b,x[k+11],S23,0x265E5A51);
        b=GG(b,c,d,a,x[k+0], S24,0xE9B6C7AA);
        a=GG(a,b,c,d,x[k+5], S21,0xD62F105D);
        d=GG(d,a,b,c,x[k+10],S22,0x2441453);
        c=GG(c,d,a,b,x[k+15],S23,0xD8A1E681);
        b=GG(b,c,d,a,x[k+4], S24,0xE7D3FBC8);
        a=GG(a,b,c,d,x[k+9], S21,0x21E1CDE6);
        d=GG(d,a,b,c,x[k+14],S22,0xC33707D6);
        c=GG(c,d,a,b,x[k+3], S23,0xF4D50D87);
        b=GG(b,c,d,a,x[k+8], S24,0x455A14ED);
        a=GG(a,b,c,d,x[k+13],S21,0xA9E3E905);
        d=GG(d,a,b,c,x[k+2], S22,0xFCEFA3F8);
        c=GG(c,d,a,b,x[k+7], S23,0x676F02D9);
        b=GG(b,c,d,a,x[k+12],S24,0x8D2A4C8A);
        a=HH(a,b,c,d,x[k+5], S31,0xFFFA3942);
        d=HH(d,a,b,c,x[k+8], S32,0x8771F681);
        c=HH(c,d,a,b,x[k+11],S33,0x6D9D6122);
        b=HH(b,c,d,a,x[k+14],S34,0xFDE5380C);
        a=HH(a,b,c,d,x[k+1], S31,0xA4BEEA44);
        d=HH(d,a,b,c,x[k+4], S32,0x4BDECFA9);
        c=HH(c,d,a,b,x[k+7], S33,0xF6BB4B60);
        b=HH(b,c,d,a,x[k+10],S34,0xBEBFBC70);
        a=HH(a,b,c,d,x[k+13],S31,0x289B7EC6);
        d=HH(d,a,b,c,x[k+0], S32,0xEAA127FA);
        c=HH(c,d,a,b,x[k+3], S33,0xD4EF3085);
        b=HH(b,c,d,a,x[k+6], S34,0x4881D05);
        a=HH(a,b,c,d,x[k+9], S31,0xD9D4D039);
        d=HH(d,a,b,c,x[k+12],S32,0xE6DB99E5);
        c=HH(c,d,a,b,x[k+15],S33,0x1FA27CF8);
        b=HH(b,c,d,a,x[k+2], S34,0xC4AC5665);
        a=II(a,b,c,d,x[k+0], S41,0xF4292244);
        d=II(d,a,b,c,x[k+7], S42,0x432AFF97);
        c=II(c,d,a,b,x[k+14],S43,0xAB9423A7);
        b=II(b,c,d,a,x[k+5], S44,0xFC93A039);
        a=II(a,b,c,d,x[k+12],S41,0x655B59C3);
        d=II(d,a,b,c,x[k+3], S42,0x8F0CCC92);
        c=II(c,d,a,b,x[k+10],S43,0xFFEFF47D);
        b=II(b,c,d,a,x[k+1], S44,0x85845DD1);
        a=II(a,b,c,d,x[k+8], S41,0x6FA87E4F);
        d=II(d,a,b,c,x[k+15],S42,0xFE2CE6E0);
        c=II(c,d,a,b,x[k+6], S43,0xA3014314);
        b=II(b,c,d,a,x[k+13],S44,0x4E0811A1);
        a=II(a,b,c,d,x[k+4], S41,0xF7537E82);
        d=II(d,a,b,c,x[k+11],S42,0xBD3AF235);
        c=II(c,d,a,b,x[k+2], S43,0x2AD7D2BB);
        b=II(b,c,d,a,x[k+9], S44,0xEB86D391);
        a=AddUnsigned(a,AA);
        b=AddUnsigned(b,BB);
        c=AddUnsigned(c,CC);
        d=AddUnsigned(d,DD);
    }
  
    var temp = WordToHex(a)+WordToHex(b)+WordToHex(c)+WordToHex(d);
  
    return temp.toLowerCase();
}

var g_debug = true
var _ = {
Storage: (function() {
 function getStorageScope(scope) {
  if (scope && scope == 'session') {
   return sessionStorage;
 } // if

 return localStorage;
} // getStorageTarget (getStorageScope)

return {
 get: function(key, scope) {
  // get the storage target
  var value = getStorageScope(scope).getItem(key);

  // if the value looks like the serialized JSON, parse it
  return (/^(\{|\[).*(\}|\])$/).test(value) ? JSON.parse(value) : value; },

  set: function(key, value, scope) {
  // if the value is an object, then stringify using JSON
  var serializable = Array.isArray(value) || typeof value == 'object';
  var storeValue = serializable ? JSON.stringify(value) : value;
  
  // save the value
   getStorageScope(scope).setItem(key, storeValue);
  },

  remove: function(key, scope) {
   getStorageScope(scope).removeItem(key);
  }
 };
})(),

Debug: (function(){
return {
html: function(html_msg){
if (g_debug){
var log = document.getElementById('debuglog')
if (!log){
log = document.createElement('div')
log.id = 'debuglog'
log.innerHTML = '<h2>Debug Log</h2>'
document.body.appendChild(log)
}
var div = document.createElement('div')
div.innerHTML = html_msg
document.body.appendChild(div)
}
},

text: function(text_msg){
if (g_debug){
var log = document.getElementById('debuglog')
if (!log){
log = document.createElement('div')
log.id = 'debuglog'
log.innerHTML = '<h2>Debug Log</h2>'
document.body.appendChild(log)
}
var pre = document.createElement('pre')
var text = document.createTextNode(text_msg)
pre.appendChild(text)
log.appendChild(pre)
}
},

};
})(),


};

function b64EncodeUnicode(str) {
 return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
  function(match, p1) { return String.fromCharCode('0x' + p1); }));
}

function getRequest() {   

   var url = location.search; //获取url中"?"符后的字串   

   var theRequest = new Object();   

   if (url.indexOf("?") != -1) {   

      var str = url.substr(1);   

      strs = str.split("&");   

      for(var i = 0; i < strs.length; i ++) {   
        var value = decodeURIComponent(strs[i].split("=")[1]);
        theRequest[strs[i].split("=")[0]] = value.replace(/\+/g,' ');   
      }   
   }   
   return theRequest;
}

function trim(str)
{
  return str.replace(/(^\s*)|(\s*$)/g,'')
}

function is_mobile(){
var s = navigator.userAgent
if (s.indexOf('Mobile')>0){
return true
}else{
return false
}
}

function Speak(s, start_callback,end_callback, rate, lang, voiceURI) {
if (window.myAndroid){
myAndroid.speak(s,rate,end_callback,true);
}else{
 try
 { 
   if (window.SpeechSynthesisUtterance){
    var utterTTS = new window.SpeechSynthesisUtterance(s)
    if (start_callback)utterTTS.onstart=start_callback
    if (end_callback)utterTTS.onend=end_callback
    if(rate)utterTTS.rate = rate
    if(lang)utterTTS.lang = lang
    else {
      utterTTS.lang = 'en_US';
}
if(voiceURI)utterTTS.voiceURI = voiceURI
else utterTTS.voiceURI = 'English United States'
    speechSynthesis.speak(utterTTS)
//debug('speak: '+s)
   }else{
     //debug("Your browser doesn't support speechSynthesis.")
   }
 }
 catch(e)
 {
   alert('Fail to speak:'+ e.message)
 }
}
}

function $language(s){
var l = 'en';
if (escape(s).indexOf( "%u" )>=0)
{
  l =  "zh" ;
}
return l;
}


function speak_o_baidu(s,onend,language,rate){
var text = encodeURI(s);
var lan = language || 'en';
var spd = rate || 5;
var tts = document.getElementById('tts_source')
if(tts==null){
var audio = document.createElement('audio')
audio.id='tts_source'
audio.controls='true'
document.body.appendChild(audio)
audio.onended = onend
tts = audio
}
tts.src="http://tts.baidu.com/text2audio?lan="+lan+"&ie=UTF-8&spd="+spd+"&text="+ text
tts.load()
//tts.play()
}

function youdao_trans2(s, onsuccess, from, to){
var appKey = '35ce28ab64674310';
var key = 'yYLi8EtO1kcBxn8LZDLVx8pWBwKMIwUP';
var salt = (new Date).getTime();
var query = s;
var str1 = appKey + query + salt +key;
var sign = MD5(str1);
jQuery.ajax({
    url: 'http://openapi.youdao.com/api',
    type: 'get',
    dataType: 'jsonp',
    data: {
        q: query,
        appKey: appKey,
        salt: salt,
        from: from,
        to: to,
        sign: sign
    },
    success: function (data) {
     if(onsuccess){
      onsuccess(data)
     }
    } 
});
}

function youdao_trans(s, onsuccess, param){
var appKey = '35ce28ab64674310';
var key = 'yYLi8EtO1kcBxn8LZDLVx8pWBwKMIwUP';
var salt = (new Date).getTime();
var query = s;
var from = 'en';
var to = 'zh';
var str1 = appKey + query + salt +key;
var sign = MD5(str1);
jQuery.ajax({
    url: 'http://openapi.youdao.com/api',
    type: 'get',
    dataType: 'jsonp',
    data: {
        q: query,
        appKey: appKey,
        salt: salt,
        from: from,
        to: to,
        sign: sign
    },
    success: function (data) {
     if(onsuccess){
      onsuccess(data, param)
     }
    } 
});
}

function baidu_translate(s, onsuccess){
var appid = '20181224000251710';
var key = 'b5yjXwXux12xBvA236z9';
var salt = (new Date).getTime();
var query = s;
var from = 'en';
var to = 'zh';
var str1 = appid + query + salt +key;
var sign = MD5(str1);
jQuery.ajax({
    url: 'http://api.fanyi.baidu.com/api/trans/vip/translate',
    type: 'get',
    dataType: 'jsonp',
    data: {
        q: query,
        appid: appid,
        salt: salt,
        from: from,
        to: to,
        sign: sign
    },
    success: function (data) {
        for(var i in data.trans_result){
         onsuccess(data.trans_result[i]);
        }
    } 
});
}

function split_text_to_be_touchable(text, ontouch){
var tokens = text.split(/\s+/)
var div = $1('div')
for(var i in tokens){
var span = $1('span')
span.appendChild($3(tokens[i]))
span.onclick = ontouch
div.appendChild(span)
div.appendChild($3(' '))
}
return div
}
