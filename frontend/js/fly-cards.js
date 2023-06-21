function animateCardFly(cardElement) {
    var targetX = Math.random() * window.innerWidth;
    var targetY = Math.random() * window.innerHeight;

    cardElement.style.position = 'absolute';
    cardElement.style.left = '50%';
    cardElement.style.top = '50%';
    cardElement.style.opacity = '0';
    cardElement.classList.add('card-image'); // Add the CSS class

    cardElement.animate(
        [
            { transform: 'translate(-50%, -50%)', opacity: '0' },
            { transform: 'translate(' + targetX + 'px, ' + targetY + 'px)', opacity: '1' }
        ],
        {
            duration: 1000,
            easing: 'ease-out'
        }
    );
}
