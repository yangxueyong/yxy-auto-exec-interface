// Fetch real repository data
async function fetchRepoData() {
    try {
        const response = await fetch('https://api.github.com/repos/yangxueyong/yxy-auto-exec-interface');
        const data = await response.json();
        
        // Update stats
        document.getElementById('stars').textContent = data.stargazers_count || 0;
        document.getElementById('watchers').textContent = data.watchers_count || 0;
        
        // Fetch commits
        const commitsResponse = await fetch('https://api.github.com/repos/yangxueyong/yxy-auto-exec-interface/commits');
        const commits = await commitsResponse.json();
        document.getElementById('commits').textContent = commits.length || 0;
        
    } catch (error) {
        console.log('Could not fetch GitHub data:', error);
    }
}

// Initialize on page load
window.addEventListener('load', () => {
    fetchRepoData();
    initializeScrollAnimations();
});

// Scroll animations
function initializeScrollAnimations() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, {
        threshold: 0.1
    });

    // Observe all animated elements
    document.querySelectorAll('.feature-card, .content-card, .timeline-item, .changelog-item').forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(el);
    });
}

// Smooth scroll for navigation
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        const href = this.getAttribute('href');
        if (href !== '#') {
            e.preventDefault();
            const target = document.querySelector(href);
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        }
    });
});

// Add active state to navigation
window.addEventListener('scroll', () => {
    const sections = document.querySelectorAll('section');
    let current = '';
    
    sections.forEach(section => {
        const sectionTop = section.offsetTop;
        const sectionHeight = section.clientHeight;
        if (pageYOffset >= sectionTop - 200) {
            current = section.getAttribute('id');
        }
    });
    
    document.querySelectorAll('.navbar-menu a').forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href').slice(1) === current) {
            link.classList.add('active');
        }
    });
});

// Add CSS class for active navigation
const style = document.createElement('style');
style.textContent = `
    .navbar-menu a.active {
        color: var(--primary);
        font-weight: 700;
    }
`;
document.head.appendChild(style);

// Performance metrics
window.addEventListener('load', () => {
    console.log('Website loaded successfully!');
    console.log('yxy-auto-exec-interface Official Website');
    console.log('Made with ❤️ by Copilot Assistant');
});
