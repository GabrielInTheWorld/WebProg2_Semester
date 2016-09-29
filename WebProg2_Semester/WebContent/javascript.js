var theType = "rectangle";
	var typeChat = "rectangle";
	var i = 0;
	var historyMap = new Map();
	var polygonPoints = [];
	var polygonPointsOnChat = [];
	var idToDelete = null;
	
	function animate(){
		console.log("animate()");
	}
	
	function setType(id){
		if(id == "polygon")
			alert("Click five times until the polygon is drawn.");
		theType = id;
		//document.getElementById(id).focus();
	}
	
	function setTypeChat(id){
		if(id == "polygon")
			alert("Click five times until the polygon is drawn.");
		typeChat = id;
	}
	
	function loadImage(){
		var dataURL = canvas.toDataURL();
		document.getElementById('canvasImg').src = dataURL;
	}
	
	function writeMessage(type, x, y, content) {
       /* var context = canvas.getContext('2d');
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.font = '18pt Calibri';
        context.fillStyle = 'black';
        context.fillText(message, 10, 25); */
        
        var id = "historyElement" + i++;
        //document.getElementById('history').innerHTML += '<p onclick="getFocus(id)" tabindex=\"0\" class="historyElement" id=' + id + '>' + content + '<br/></p>';
        var para = document.createElement("p");
        para.setAttribute("id", id);
        para.setAttribute("onclick", "getFocus(id)");
        para.setAttribute("tabindex","0");
        para.setAttribute("class", "historyElement");
      //  var node = document.createTextNode("onclick=\"getFocus(id)\" tabindex=\"0\" class=\"historyElement\" id=" + id);
        var node = document.createTextNode(content);
        para.appendChild(node);
        
        var element = document.getElementById("history");
        element.appendChild(para);
      	toHistory(id, content);
      }
	
		function getFocus(elementID){
			console.log("elementID: ", elementID);
			document.getElementById(elementID).focus();
			idToDelete = elementID;
		}
	
		function onDraw(x, y, x2, y2, onType, sending){
			var type = null;
			if(onType != null){
				type = onType;
			}else{
				type = theType;
			}
			var a, b;
			var width, height;
			if(context && type){
				if(type == "line"){
					context.beginPath();
					context.moveTo(x, y);
					context.lineTo(x2, y2);
					context.closePath();
					context.stroke();
					
					var content = JSON.stringify({"type":type, "startX":x, "startY":y, "endX":x2, "endY":y2});
					loadImage();
					if(sending){
						writeMessage(type, x, y, content);
						toDraw(content);
					}
				}
				else if(type == "rectangle"){
					if(x < x2){
						a = x;
						width = x2 - x;
					}else{
						a = x2;
						width = x - x2;
					}
					
					if(y < y2){
						b = y;
						height = y2 - y;
					}else{
						b = y2;
						height = y - y2;
					}
					
					context.strokeRect(a, b, width, height);
					
					var content = JSON.stringify({"type":type, "x":a, "y":b, "width":width, "height":height});
					loadImage();
					if(sending){
						toDraw(content);
						writeMessage(type, a, b, content);
					}
				}else if(type == "circle"){
					var radius;
					if(y2 != null){
						width = x2 - x;
						height = y2 - y;
						radius = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
					}else{
						radius = x2;
					}
					context.beginPath();
					context.arc(x, y, radius, 0, 2 * Math.PI);
					context.closePath();
					context.stroke();
					
					var content = JSON.stringify({"type":type, "x":x, "y":y, "radius":radius});
					loadImage();
					if(sending){
						writeMessage(type, x, y, content);
						toDraw(content);
					}
				}else if(type == "freeHand"){
					context.fillRect(x, y, 1, 1);
					
				//	writeMessage(type, x, y, "size: " + 1);
					loadImage();
				}else if(type == "polygon"){
					context.beginPath();
					context.moveTo(polygonPoints[0].x, polygonPoints[0].y);
					context.lineTo(polygonPoints[1].x, polygonPoints[1].y);
					context.lineTo(polygonPoints[2].x, polygonPoints[2].y);
					context.lineTo(polygonPoints[3].x, polygonPoints[3].y);
					context.lineTo(polygonPoints[4].x, polygonPoints[4].y);
					context.closePath();
					context.stroke();
					
					loadImage();
					if(sending){
						writeMessage(type, 0, 0, "Array: " + polygonPoints.toString());
						toDraw(polygonPoints);
					}
					while(polygonPoints.length > 0){
						polygonPoints.pop();
					}
				}
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
		var canvas = document.getElementById('canvas');
		var context = canvas.getContext('2d');
		
		var pointer;
		var isPressing = false;
		
		canvas.addEventListener('mousemove', function(evt) {
	        var mousePos = getMousePos(canvas, evt);
	        if(isPressing && theType == "freeHand"){
	        	this.style.cursor='pointer';
	        	onDraw(mousePos.x, mousePos.y, 0, 0, null, true);
	        }
	        var message = 'Mouse position: ' + mousePos.x + ',' + mousePos.y;
	      }, false);
		
		
		canvas.addEventListener('mousedown', function(event){
			isPressing = true;
			pointer = getMousePos(canvas, event);
			var p = getMousePos(canvas, event);
			
			if(theType == "polygon"){
				polygonPoints.push(p);
			}
		}, false);
		canvas.addEventListener('mouseup', function(event){
			isPressing = false;
			var pointer2 = getMousePos(canvas, event);

			x = pointer.x;
			y = pointer.y;
			
			x2 = pointer2.x;
			y2 = pointer2.y;
			if(theType != "polygon")
				onDraw(x, y, x2, y2, null, true);
			else if(polygonPoints.length == 5)
				onDraw(0, 0, 0, 0, null, true);
		}, false);
		
		
		var x, y;
		var x2, y2;
		var loc = window.location, uri;
		if(loc.protocol == "https:"){
			uri = "wss://";
		}else{
			uri = "ws://";
		}
		uri += loc.host + loc.pathname;
		uri += "websocket";
		console.log("uri", uri);
		var webSocket = new WebSocket(
			uri
				//	"ws://195.37.110.152:8080/WebProg2_Semester/websocket"
			//"ws://195.37.49.24/sos16_02/websocket"
		);
		
		webSocket.onerror = function(event){
			onError(event)
		};
		
		webSocket.onopen = function(event){
			onOpen(event)
		};
		
		webSocket.onmessage = function(message){
			console.log(".onmessage:", message),
			onMessage(message)
		};
		
		function onMessage(message){
			console.log("onMessage()");
			console.log(message, ": ", message.data);
			console.log("Type: ", message.data.type);
			var jsonData = JSON.parse(message.data);
			console.log("jsonData: ", jsonData);
			if(jsonData != null){
				if(jsonData.type == "line"){
					console.log("line", jsonData);
					onDraw(jsonData.startX, jsonData.startY, jsonData.endX, jsonData.endY, "line", false);
				}else if(jsonData.type == "rectangle"){
					console.log("rectangle", jsonData);	
					onDraw(jsonData.x, jsonData.y, (jsonData.x + jsonData.width), (jsonData.y + jsonData.height), "rectangle", false);
				}else if(jsonData.type == "circle"){
					console.log("circle", jsonData);	
					onDraw(jsonData.x, jsonData.y, jsonData.radius, null, "circle", false);
				}else if(jsonData.type == "polygon"){
					console.log("polygon", jsonData);
					this.polygonPoints = jsonData.content;
					onDraw(0, 0, 0, 0, "polygon", false);
				}else if(jsonData.type == "freeHand"){
					console.log("freeHand", jsonData);	
				}else if(jsonData.type == "chat"){
				//messagesTextArea.value += jsonData.username + ": " + jsonData.message + "\n";
				//messagesTextArea.value += "<img src=\"" + jsonData.imageURL + "\" width=\"100\" height=\"100\" />\n";	
					document.getElementById("chatBox").innerHTML +=  jsonData.username + ": " + jsonData.message + "<br />";
					if(jsonData.imageURL != null)
						document.getElementById("chatBox").innerHTML += "<img src=\"" + jsonData.imageURL + "\" width=\"100\" height=\"100\" /><br />";
				}
			}else
				console.log("message is empty");
		}
		
		function onOpen(event){
			document.getElementById('chatBox').innerHTML = 'Now connection established<br />';
		}
		
		function onError(event){
			alert(event.data);
		}
		
		var canvasChat = document.getElementById('chatCanvas');
		var contextChat = canvasChat.getContext('2d');
		
		var pointerChat;
		var isPressingOnChat = false;
		
		canvasChat.addEventListener("mousemove", function(event){
			if(isPressingOnChat && typeChat == "freeHand"){
				var move = getMousePos(canvasChat, event);
				onDrawChat(move.x, move.y, 0, 0, contextChat);
			}
		}, false);
		
		canvasChat.addEventListener("mousedown", function(event){
			isPressingOnChat = true;
			pointerChat = getMousePos(canvasChat, event);
			
			if(typeChat == "polygon"){
				var p = getMousePos(canvasChat, event);
				polygonPointsOnChat.push(p);
			}
		}, false);
		
		canvasChat.addEventListener("mouseup", function(event){
			isPressingOnChat = false;
			var pointer2 = getMousePos(canvasChat, event);
			
			if(typeChat != "polygon")
				onDrawChat(pointerChat.x, pointerChat.y, pointer2.x, pointer2.y, contextChat);
			else if(polygonPointsOnChat.length == 5){
				onDrawChat(0, 0, 0, 0, contextChat);
			}
		}, false);
		
		function onClearChat(width, height, context){
			context.clearRect(0, 0, width, height);
		}
		function onDrawChat(x, y, x2, y2, context){
			var a, b;
			var width, height;
			if(context){
				if(typeChat == "line"){
					context.beginPath();
					context.moveTo(x, y);
					context.lineTo(x2, y2);
					context.closePath();
					context.stroke();
		//			writeMessage(type, x, y, "EndX: " + x2 + ", EndY: " + y2);
				}
				else if(typeChat == "rectangle"){
					if(x < x2){
						a = x;
						width = x2 - x;
					}else{
						a = x2;
						width = x - x2;
					}
					
					if(y < y2){
						b = y;
						height = y2 - y;
					}else{
						b = y2;
						height = y - y2;
					}
					
					context.strokeRect(a, b, width, height);
			//		writeMessage(type, a, b, " width: " + width + " height: " + height);
			//		loadImage();
				}else if(typeChat == "circle"){
					width = x2 - x;
					height = y2 - y;
					var radius = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
					context.beginPath();
					context.arc(x, y, radius, 0, 2 * Math.PI);
					context.closePath();
					context.stroke();
			//		writeMessage(type, x, y, "radius: " + radius);
			//		loadImage();
				}else if(typeChat == "freeHand"){
					context.fillRect(x, y, 1, 1);
				}else if(typeChat == "polygon"){
					context.beginPath();
					context.moveTo(polygonPointsOnChat[0].x, polygonPointsOnChat[0].y);
					context.lineTo(polygonPointsOnChat[1].x, polygonPointsOnChat[1].y);
					context.lineTo(polygonPointsOnChat[2].x, polygonPointsOnChat[2].y);
					context.lineTo(polygonPointsOnChat[3].x, polygonPointsOnChat[3].y);
					context.lineTo(polygonPointsOnChat[4].x, polygonPointsOnChat[4].y);
					context.closePath();
					context.stroke();
					
				//	writeMessage(type, 0, 0, "Array: " + polygonPoints.toString());
				//	loadImage();
					
					while(polygonPointsOnChat.length > 0){
						polygonPointsOnChat.pop();
					}
				}
			}
		}
		
		function toHistory(id, content){
			historyMap.set(id, content);
		}
		
		function removeFromHistory(){
			historyMap.delete(idToDelete);
			
			var parent = document.getElementById('history');
			var child = document.getElementById(idToDelete);
			console.log("child which will destroyed: ", child);
			parent.removeChild(child);
			
			drawFromHistory();
			
			idToDelete = null;
			loadImage();
		}
		
		function clearHistory(){
			var parent = document.getElementById("history");
			historyMap.forEach(function(value, key, historyMap) {
				historyMap.delete(key);
				var child = document.getElementById(key);
				parent.removeChild(child);
			});
			console.log("historyLength: ", historyMap.size);
			context.clearRect(0, 0, canvas.width, canvas.height);
			loadImage();
		}
		
		function drawFromHistory(){
			context.clearRect(0, 0, canvas.width, canvas.height);
			
			historyMap.forEach(function(value, key, map) {
				var content = JSON.parse(value);
				console.log("value: ", content);
				afterDelete(content);
			})
		}
		
		function afterDelete(content){
			console.log("get from history: ", content);
			
			if(content.type == "line"){
				context.beginPath();
				context.moveTo(content.startX, content.startY);
				context.lineTo(content.endX, content.endY);
				context.closePath();
				context.stroke();
			}else if(content.type == "rectangle"){
				context.strokeRect(content.x, content.y, content.width, content.height);
			}else if(content.type == "circle"){
				context.beginPath();
				context.arc(content.x, content.y, content.radius, 0, 2 * Math.PI);
				context.closePath();
				context.stroke();
			}else if(content.type == "polygon"){
				
			}
		}
		
		function toDraw(content){
			var message = JSON.stringify({"type":theType, "content":content}, null, "\t");
			webSocket.send(message);
		}
		
		function save(){
		/*	var img = canvas.toDataURL();
			var url = img.src.replace(/^data:image\/[^;]/, 'data:application/octet-stream');
			window.open(url);*/
			var historyToD = JSON.stringify(historyMap);
			console.log("save(): ", historyToD);
			var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(historyToD);
			var dlAnchorElem = document.getElementById('downloadAnchorElem');
			dlAnchorElem.setAttribute("href", dataStr);
			dlAnchorElem.setAttribute("download", "scene.json");
			//dlAnchorElem.click();
		}
		
		function start(){
			var user = document.getElementById("username").value;
			var text = document.getElementById("userinput").value;
			var dataURL = canvasChat.toDataURL();
			
			var content = JSON.stringify({"username":user, "message":text, "imageURL":dataURL}, null, "\t");
			console.log("content: ", content);
			var c = JSON.parse(content);
			console.log("geparsed: ", c);
			var message = JSON.stringify({"type":"chat", "content":c}, null, "\t");
			webSocket.send(message);
			
			console.log("message: ", message);
			userinput.value = "";
			dataURL = null;
			onClearChat(canvasChat.width, canvasChat.height, contextChat);
		}