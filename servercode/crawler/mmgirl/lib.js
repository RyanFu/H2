function replaceWithBase64(img) {
    var canvas = document.createElement("canvas");
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0);    
    var dataURL = canvas.toDataURL("image/png");
    img.src = dataURL;    
}
