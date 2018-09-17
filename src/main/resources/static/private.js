var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        //setConnected(true);
       var firstName =  $("#send1").parent().find("label").text();
        var secondName = $("#send2").parent().find("label").text();
        console.log('Connected: ' + frame);
        stompClient.subscribe('/queue/'+firstName+'/chartPrivate', function (chartContent) {
            selfSpeaking(chartContent,$("#chartWin2"));
            setTimeout(showGreeting(chartContent,$("#chartWin1")),1000);

        });
        stompClient.subscribe('/queue/'+secondName+'/chartPrivate', function (chartContent) {
            //显示自己对话框
            selfSpeaking(chartContent,$("#chartWin1"));
            //显示对方对话框
            setTimeout(showGreeting(chartContent,$("#chartWin2")),1000);

        });
    });
}

/*function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}*/

function sendName(ele,listener) {
     var speaker =ele.parent().find("label").text();
     var content = ele.parent().find("input").val();
    var  param = '{"title":null,"time":null,"content":"'+content+'","speaker":"'+speaker+'","listener":"'+listener+'"}';
    stompClient.send("/app/chartPrivate/"+speaker, {},param);
}
//自己的话出现在对方对话框
function showGreeting(message,ele) {
    return function (){
        var content = JSON.parse(message.body).content;
        var speaker = JSON.parse(message.body).speaker;
        ele.append("<tr style='padding-left: 25px;'><td style='background-color:#FF6D59;'>"+speaker+" : " + content + "</td></tr>");
    }
}
//自己的话显示在自己的对话框
function selfSpeaking(message,ele){
    var content = JSON.parse(message.body).content;
    ele.append("<tr style='padding-left: 25px;'><td style='background-color:#AEEEEE;text-align: right;'>我 : " + content + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    connect();
    //$( "#disconnect" ).click(function() { disconnect(); });
    $( "#send1" ).click( function(){sendName($("#send1"),"小明")} );
    $( "#send2" ).click( function(){sendName($( "#send2"),"张老师" )} );
});