/**
 * handler for index.html
 */

	var type = "rectangle";
	var i = 0;
	
	var canvas = document.getElementById('canvas');
	var context = canvas.getContext('2d');
	var x, y;
	var x2, y2;
	
	var pointer;
	
	function setType(id){
		type = id;
	}
	
	function loadImage(){
		var dataURL = canvas.toDataURL();
		document.getElementById('canvasImg').src = dataURL;
	}
	
	function writeMessage(type, x, y, width, height) {
       /* var context = canvas.getContext('2d');
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.font = '18pt Calibri';
        context.fillStyle = 'black';
        context.fillText(message, 10, 25); */
        
        document.getElementById('history').innerHTML += '<p onclick="getFocus(id)" class="historyElement" id=' + i++ + '>' + type + "\tx: " + x + " y: " + y + " width: " + width + " height: " + height + '<br/></p>';
      }
	
		function getFocus(elementID){
			console.log("elementID: ", elementID);
			document.getElementById(elementID).focus();
			
		}
	
		function onDraw(x, y, width, height){
			if(context){
				context.strokeRect(x, y, width, height);
				writeMessage("Rect", x, y, width, height);
				loadImage();
			}
		}
		
		function getMousePos(canvas, event){
			var rect = canvas.getBoundingClientRect();
			//console.log(rect);
			return {
				x: event.clientX - rect.left,
				y: event.clientY - rect.top
			};
		}
	
		
		canvas.addEventListener('mousemove', function(evt) {
	        var mousePos = getMousePos(canvas, evt);
	        var message = 'Mouse position: ' + mousePos.x + ',' + mousePos.y;
	      }, false);
		
		
		canvas.addEventListener('mousedown', function(event){
			pointer = getMousePos(canvas, event);
		}, false);
		
		canvas.addEventListener('mouseup', function(event){
			var pointer2 = getMousePos(canvas, event);

			if(pointer.x < pointer2.x){
				x = pointer.x;
				x2 = pointer2.x;
			}else{
				x = pointer2.x;
				x2 = pointer.x;
			}
			var width = x2 - x;
			
			if(pointer.y < pointer2.y){
				y = pointer.y;
				y2 = pointer2.y;
			}else{
				y = pointer2.y;
				y2 = pointer.y;
			}
			var height = y2 - y;
			
			onDraw(x, y, width, height);
		}, false);
		

		var webSocket = new WebSocket(
			'ws://localhost:8080/WebProg2_Semester/websocket'		
		);
		
		webSocket.onerror = function(event){
			onError(event)
		};
		
		webSocket.onopen = function(event){
			onOpen(event)
		};
		
		webSocket.onmessage = function(message){
			onMessage(message)
		};
		
		function onMessage(message){
			console.log(message, ": ", message.data);
			var jsonData = JSON.parse(message.data);
			console.log(jsonData);
			if(jsonData != null)
				messagesTextArea.value += jsonData.username + ": " + jsonData.message + "\n";
			else
				console.log("message is empty");
		}
		
		function onOpen(event){
			document.getElementById('messagesTextArea').innerHTML = 'Now connection established\n';
		}
		
		function onError(event){
			alert(event.data);
		}
		
		function start(){
			var text = document.getElementById("userinput").value;
			webSocket.send(text);
			
			userinput.value = "";
		}