// fly-cards.js

// Function to animate card fly
export default function animateCardFly(cardElement) {
    // Calculate random target position for the card
    const targetX = Math.random() * window.innerWidth;
    const targetY = Math.random() * window.innerHeight;

    // Set initial position and opacity of the card
    cardElement.style.position = 'absolute';
    cardElement.style.left = '50%';
    cardElement.style.top = '50%';
    cardElement.style.opacity = '0';

    // Animate the card fly
    cardElement.animate(
        [
            { transform: 'translate(-50%, -50%)', opacity: '0' },
            { transform: `translate(${targetX}px, ${targetY}px)`, opacity: '1' }
        ],
        {
            duration: 1000, // Animation duration in milliseconds
            easing: 'ease-out' // Animation easing
        }
    );
}
