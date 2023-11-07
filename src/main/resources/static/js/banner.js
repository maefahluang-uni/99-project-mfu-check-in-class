var imgs = document.querySelectorAll('.slider img');
var dots = document.querySelectorAll('.dot');
var currentImg = 0; 
const interval = 3000; 
const updator = () => {
  changeSlide("increment", 1)
}
var timer = setInterval(updator, interval);

function changeSlide(mode, n) {
    for (var i = 0; i < imgs.length; i++) { 
      imgs[i].style.opacity = 0;
      dots[i].className = dots[i].className.replace(' active', '');
    }
    if (mode == "set") {
      clearInterval(updator);
      timer = setInterval(updator, interval);
      currentImg = n;
    } else if (mode == "increment") {
      if(n == -1){
        if(currentImg == 0){
          currentImg = 2;
        }else{
          currentImg += n; 
        }
      }else{
        currentImg += n; 
      }
    }
    currentImg %= imgs.length;
    console.log(currentImg)
    imgs[currentImg].style.opacity = 1;
    dots[currentImg].className += ' active';
}