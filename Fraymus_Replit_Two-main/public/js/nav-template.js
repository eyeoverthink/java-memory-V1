document.addEventListener('DOMContentLoaded', () => {
    // Get all tab buttons
    const tabButtons = document.querySelectorAll('.nav-link');

    // Add click handlers
    tabButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();

            // Remove active class from all buttons
            tabButtons.forEach(btn => btn.classList.remove('active'));

            // Add active class to clicked button
            button.classList.add('active');

            // Show corresponding content
            const targetId = button.getAttribute('href').substring(1);
            const panels = document.querySelectorAll('.tab-content');
            panels.forEach(panel => {
                panel.style.display = panel.id === targetId ? 'block' : 'none';
            });
        });
    });
});