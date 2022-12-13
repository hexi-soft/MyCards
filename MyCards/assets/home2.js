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

function on_touch_token(){
var token = this.innerText;
youdao_trans(token, function(result){
var resultHTML =  result.translation
$('#tip_wnd_content').html(resultHTML)
$('#tip_wnd').removeClass('hidden').focus()
}, null);
}

$(function(){
$('a').click(function(){
$('this').addClass('visited');
myAndroid.load_url($(this).attr('href'), $(this).text());
});
var tip_wnd = $('<div/>').attr('id','tip_wnd').addClass('hidden');
tip_wnd.append($('<div/>').html('X').attr('id','tip_wnd_close'));
tip_wnd.append($('<div/>').attr('id','tip_wnd_content'));
$('body').append(tip_wnd);
$('#tip_wnd_close').click(function(){$('#tip_wnd').addClass('hidden')});

var interested_tag = 'p'
$(interested_tag).bind('adjunct', function(evt){
var text = this.innerText;
var div = split_text_to_be_touchable(text, on_touch_token)
this.replaceChild(div, this.firstChild)
});
$(interested_tag).trigger('adjunct', null);
debug('o')
});