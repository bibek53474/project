// Fashion Retail - Product rendering and simple interactions
(function() {
    'use strict';

    const state = {
        products: [],
        filtered: [],
        filters: {
            search: '',
            category: 'All',
            sort: 'featured'
        }
    };

    function $(selector) {
        return document.querySelector(selector);
    }

    function $all(selector) {
        return Array.from(document.querySelectorAll(selector));
    }

    function formatPrice(value) {
        return `$${value.toFixed(2)}`;
    }

    function ratingStars(rating) {
        const full = Math.floor(rating);
        const half = rating - full >= 0.5 ? 1 : 0;
        const empty = 5 - full - half;
        return '★'.repeat(full) + (half ? '½' : '') + '☆'.repeat(empty);
    }

    function getPlaceholderDataUri() {
        // Lightweight SVG placeholder (base64)
        return 'data:image/svg+xml;base64,' +
            btoa(`<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="600" height="400">
  <defs>
    <linearGradient id="g" x1="0" x2="1">
      <stop stop-color="#e9ecef" offset="0"/>
      <stop stop-color="#dee2e6" offset="1"/>
    </linearGradient>
  </defs>
  <rect width="100%" height="100%" fill="url(#g)"/>
  <g fill="#adb5bd" font-family="Segoe UI, Arial, sans-serif" text-anchor="middle">
    <text x="300" y="210" font-size="28" font-weight="600">Image unavailable</text>
    <text x="300" y="245" font-size="16">Fashion Retail</text>
  </g>
</svg>`);
    }

    function safeImageSrc(src) {
        return src && typeof src === 'string' && src.trim().length > 0 ? src : getPlaceholderDataUri();
    }

    function renderProducts(products) {
        const grid = $('#productsGrid');
        if (!grid) return;

        if (!products.length) {
            grid.innerHTML = `<div class="col-12"><div class="alert alert-info">No products found.</div></div>`;
            return;
        }

        grid.innerHTML = products.map(p => `
            <div class="col-12 col-sm-6 col-md-4 col-lg-3 mb-4">
                <div class="card h-100 shadow-custom">
                    <div class="position-relative">
                        <img
                            src="${safeImageSrc(p.image)}"
                            alt="${p.name}"
                            class="card-img-top"
                            style="height: 220px; object-fit: cover; background:#f8f9fa;"
                            loading="lazy"
                            onerror="this.onerror=null; this.src='${getPlaceholderDataUri()}';"
                        >
                        ${p.tag ? `<span class="badge bg-dark position-absolute top-0 start-0 m-2">${p.tag}</span>` : ''}
                    </div>
                    <div class="card-body d-flex flex-column">
                        <h6 class="text-muted mb-1">${p.category}</h6>
                        <h5 class="card-title mb-2">${p.name}</h5>
                        <div class="mb-2" style="color: #f4c150;">${ratingStars(p.rating)}</div>
                        <div class="d-flex align-items-center justify-content-between mt-auto">
                            <span class="fw-bold">${formatPrice(p.price)}</span>
                            <button class="btn btn-primary btn-sm" data-id="${p.id}">
                                <i class="bi bi-bag"></i> Add to Bag
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');

        // Bind add-to-bag buttons
        $all('[data-id]').forEach(btn => {
            btn.addEventListener('click', () => {
                if (window.Toast) {
                    window.Toast.success('Added to bag');
                }
            });
        });
    }

    function applyFilters() {
        const { products, filters } = state;
        let result = [...products];

        // Search
        if (filters.search) {
            const q = filters.search.toLowerCase();
            result = result.filter(p =>
                p.name.toLowerCase().includes(q) ||
                p.category.toLowerCase().includes(q)
            );
        }

        // Category
        if (filters.category && filters.category !== 'All') {
            result = result.filter(p => p.category === filters.category);
        }

        // Sort
        switch (filters.sort) {
            case 'price_asc':
                result.sort((a, b) => a.price - b.price);
                break;
            case 'price_desc':
                result.sort((a, b) => b.price - a.price);
                break;
            case 'rating':
                result.sort((a, b) => b.rating - a.rating);
                break;
            default:
                // featured - keep JSON order
                break;
        }

        state.filtered = result;
        renderProducts(result);
    }

    function renderCategories() {
        const select = $('#categoryFilter');
        if (!select) return;
        const categories = ['All', ...new Set(state.products.map(p => p.category))];
        select.innerHTML = categories.map(c => `<option value="${c}">${c}</option>`).join('');
    }

    function bindControls() {
        const search = $('#searchInput');
        const category = $('#categoryFilter');
        const sort = $('#sortSelect');

        if (search) {
            search.addEventListener('input', window.debounce(e => {
                state.filters.search = e.target.value.trim();
                applyFilters();
            }, 200));
        }
        if (category) {
            category.addEventListener('change', e => {
                state.filters.category = e.target.value;
                applyFilters();
            });
        }
        if (sort) {
            sort.addEventListener('change', e => {
                state.filters.sort = e.target.value;
                applyFilters();
            });
        }
    }

    async function loadProducts() {
        try {
            const res = await fetch('/data/products.json', { cache: 'no-store' });
            const data = await res.json();
            state.products = data;
            renderCategories();
            applyFilters();
        } catch (e) {
            const grid = $('#productsGrid');
            if (grid) {
                grid.innerHTML = `<div class="col-12"><div class="alert alert-danger">Failed to load products.</div></div>`;
            }
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        bindControls();
        loadProducts();
    });
})();


