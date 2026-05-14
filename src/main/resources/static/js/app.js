let currentUser = null;
let currentFarm = null;
let tooltip = null;
let allItems = [];
let caughtIds = [];
let currentCategory = "Риба";

document.addEventListener("DOMContentLoaded", () => {
    tooltip = document.getElementById("stardew-tooltip");

    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("register-form");
    const createFarmBtn = document.getElementById("create-farm-btn");
    const logoutBtn = document.getElementById("logout-btn");
    const backToLoginBtn = document.getElementById("back-to-login");

    if (loginForm) loginForm.onsubmit = (e) => handleAuth(e, '/api/users/login');
    if (registerForm) registerForm.onsubmit = (e) => handleAuth(e, '/api/users');
    if (createFarmBtn) createFarmBtn.onclick = createNewFarm;

    document.getElementById("link-to-register").onclick = (e) => {
        e.preventDefault();
        toggleAuthCards(true);
    };

    document.getElementById("link-to-login").onclick = (e) => {
        e.preventDefault();
        toggleAuthCards(false);
    };

    if (logoutBtn) {
        logoutBtn.onclick = () => {
            currentFarm = null;
            showFarmSelectionScreen();
        };
    }

    if (backToLoginBtn) {
        backToLoginBtn.onclick = () => window.location.href = "/logout";
    }

    const tabButtons = document.querySelectorAll('.tab-btn');
    tabButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            tabButtons.forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');

            currentCategory = e.target.getAttribute('data-category');
            renderGrid();
        });
    });
    checkCurrentSession();
});

async function handleAuth(e, url) {
    e.preventDefault();
    const isReg = url === '/api/users';

    const data = {
        username: document.getElementById(isReg ? "reg-username" : "login-username").value,
        password: document.getElementById(isReg ? "reg-password" : "login-password").value
    };
    if (isReg) data.email = document.getElementById("reg-email").value;

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            currentUser = await response.json();
            showFarmSelectionScreen();
        } else {
            alert(await response.text());
        }
    } catch (err) {
        console.error("Сервер не відповідає", err);
    }
}

async function checkCurrentSession() {
    try {
        const response = await fetch('/api/users/me');
        if (response.ok) {
            currentUser = await response.json();
            showFarmSelectionScreen();
        }
    } catch (err) {
        console.log("Сесії немає, показуємо форму входу.");
    }
}

function showFarmSelectionScreen() {
    hideAll();
    document.getElementById("farm-selection-screen").classList.remove("hidden");
    renderFarmsList();
}

function renderFarmsList() {
    const grid = document.getElementById("farms-grid");
    grid.innerHTML = "";

    if (currentUser.farms && currentUser.farms.length > 0) {
        currentUser.farms.forEach(farm => {
            const card = document.createElement("div");
            card.className = "farm-card";
            const name = farm.farmName || "Без назви";
            const type = farm.farmType || "Standard";

            card.innerHTML = `
                <div class="delete-farm-btn" title="Видалити ферму">×</div>
                <h3>${farm.farmName || "Без назви"}</h3>
                <p>Тип: ${translateType(farm.farmType)}</p>
            `;

            card.onclick = () => selectFarm(farm);

            const deleteBtn = card.querySelector('.delete-farm-btn');
            deleteBtn.onclick = (e) => {
                e.stopPropagation();
                confirmDeleteFarm(farm.id, farm.farmName);
            };

            grid.appendChild(card);
        });
    } else {
        grid.innerHTML = "<p>У вас ще немає ферм. Створіть першу!</p>";
    }
}

async function createNewFarm() {
    const nameInput = document.getElementById("new-farm-name");
    const typeSelect = document.getElementById("new-farm-type");

    if (!nameInput || !typeSelect) return;

    const farmName = nameInput.value;
    const farmType = typeSelect.value;

    if (!farmName) return alert("Введіть назву ферми!");

    const farmData = {
        userId: currentUser.id,
        farmName: farmName,
        farmType: farmType
    };

    console.log("Відправляємо на сервер створення ферми:", farmData);

    try {
        const response = await fetch('/api/farms', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(farmData)
        });

        if (response.ok) {
            const newFarm = await response.json();
            alert(`Ферму '${newFarm.farmName}' успішно створено!`);
            location.reload();
        } else {
            alert("Помилка: " + await response.text());
        }
    } catch (err) {
        console.error("Помилка запиту", err);
    }
}

function selectFarm(farm) {
    currentFarm = farm;
    hideAll();
    document.getElementById("app-screen").classList.remove("hidden");
    document.getElementById("farm-display-name").innerText = `Ферма: ${farm.farmName}`;
    document.getElementById("user-display-name").innerText = `Фермер: ${currentUser.username || "Фермер"}`;

    currentCategory = "Риба";
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.querySelector('[data-category="Риба"]').classList.add('active');

    loadItems();
}

async function loadItems() {
    const grid = document.getElementById("collection-grid");
    grid.innerHTML = "Завантаження...";

    try {
        const itemsRes = await fetch('/api/items');
        allItems = await itemsRes.json();
        const progressRes = await fetch(`/api/progress/${currentFarm.id}`);
        const caughtItems = await progressRes.json();
        caughtIds = caughtItems.map(i => i.id);

        renderGrid();
    } catch (err) {
        console.error("Помилка завантаження", err);
        grid.innerHTML = "Не вдалося завантажити предмети.";
    }

    updateProgressUI();
}

function renderGrid() {
    const grid = document.getElementById("collection-grid");
    grid.innerHTML = "";

    const folderMap = {
        "Риба": "fish",
        "Їжа": "dish",
        "Мінерали": "minerals",
        "Артефакти": "artifacts",
        "Відправка": "shipped"
    };

    const currentFolder = folderMap[currentCategory] || "items";

    const filteredItems = allItems.filter(item => {
        if (!item.category) return false;

        let catName = "";
        if (typeof item.category === 'object') {
            catName = item.category.name || "";
        } else {
            catName = item.category;
        }

        return catName.trim().toLowerCase() === currentCategory.trim().toLowerCase();
    });

    if (filteredItems.length === 0) {
        grid.innerHTML = `<p class="empty-category-message">У цій категорії поки немає предметів.</p>`;
        return;
    }

    filteredItems.forEach(item => {
        const div = document.createElement("div");
        div.className = "item-card";
        if (caughtIds.includes(item.id)) div.classList.add("caught");

        div.innerHTML = `<img src="/images/${currentFolder}/${item.imageUrl}">`;

        div.onmouseover = (e) => showStardewTooltip(item, e);
        div.onmousemove = (e) => moveStardewTooltip(e);
        div.onmouseout = () => hideStardewTooltip();

        div.onclick = () => toggleItemProgress(item, div);

        grid.appendChild(div);
    });
}

function showStardewTooltip(item, e) {
    if (!tooltip) return;

    tooltip.querySelector(".tooltip-name").innerText = item.name || "Невідомий предмет";

    const description = item.description || "Опис відсутній";

    let seasonsHTML = "";
    const seasonsData = item.seasons;
    if (seasonsData) {
        const seasonsArray = seasonsData.split(',').map(s => s.trim());
        seasonsHTML = '<div class="seasons-container">' +
            seasonsArray.map(s => `<span class="season-tag ${getSeasonClass(s)}">${s}</span>`).join('') +
            '</div>';
    }

    const locationData = item.howToGet;
    const locationHTML = locationData ? `<div class="info-row">📍 <span>${locationData}</span></div>` : "";

    const priceData = item.sellPrice;
    const priceHTML = priceData ? `<div class="info-row">💰 <span class="price-text">${priceData}g</span></div>` : "";

    tooltip.querySelector(".tooltip-description").innerHTML = `
       <p class="main-desc">${description}</p>
        ${seasonsHTML}
        ${locationHTML}
        ${priceHTML}
    `;

    let categoryName = "Риба";
    if (item.category) {
        categoryName = typeof item.category === 'object' ? (item.category.name || "Риба") : item.category;
    }
    tooltip.querySelector(".tooltip-type").innerText = categoryName;

    tooltip.classList.remove("hidden");
    moveStardewTooltip(e);
}

async function toggleItemProgress(item, element) {
    const isCaught = element.classList.contains("caught");

    const url = isCaught ? `/api/progress/${currentFarm.id}/items/${item.id}` : '/api/progress';
    const method = isCaught ? 'DELETE' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: isCaught ? null : JSON.stringify({ farmId: currentFarm.id, itemId: item.id })
        });

        if (response.ok) {
            element.classList.toggle("caught");
            if (isCaught) {
                caughtIds = caughtIds.filter(id => id !== item.id);
            } else {
                caughtIds.push(item.id);
                const achievements = await response.json();
                if (achievements && achievements.length > 0) {
                    achievements.forEach(text => showAchievementPopup(text));
                }
            }
        } else {
            alert("Помилка на сервері.");
        }
    } catch (err) {
        console.error("Помилка прогресу", err);
    }

    updateProgressUI();
}

function getSeasonClass(season) {
    const s = season.toLowerCase();
    if (s.includes('весна')) return 'spring';
    if (s.includes('літо')) return 'summer';
    if (s.includes('осінь')) return 'fall';
    if (s.includes('зима')) return 'winter';
    return '';
}

function moveStardewTooltip(e) {
    if (!tooltip) return;

    const mouseX = e.clientX;
    const mouseY = e.clientY;

    const tooltipWidth = tooltip.offsetWidth;
    const tooltipHeight = tooltip.offsetHeight;

    const windowWidth = window.innerWidth;
    const windowHeight = window.innerHeight;

    let posX = mouseX + 20;
    let posY = mouseY + 20;

    if (posX + tooltipWidth > windowWidth) {
        posX = mouseX - tooltipWidth - 20;
    }

    if (posY + tooltipHeight > windowHeight) {
        posY = mouseY - tooltipHeight - 20;
    }

    tooltip.style.left = `${posX}px`;
    tooltip.style.top = `${posY}px`;
}

function hideStardewTooltip() {
    if (tooltip) tooltip.classList.add("hidden");
}

function toggleAuthCards(isRegister) {
    document.getElementById("login-card").classList.toggle("hidden", isRegister);
    document.getElementById("register-card").classList.toggle("hidden", !isRegister);
}

function hideAll() {
    document.querySelectorAll(".screen").forEach(s => s.classList.add("hidden"));
}

function translateType(type) {
    const dict = { 'Standard': 'Стандартна', 'Riverland': 'Річкова', 'Forest': 'Лісова', 'Hill-top': 'Гірська', 'Wilderness': 'Дика' };
    return dict[type] || type;
}

async function confirmDeleteFarm(farmId, farmName) {
    if (!confirm(`Ви впевнені, що хочете видалити ферму "${farmName}"? Весь прогрес буде видалено назавжди!`)) {
        return;
    }

    try {
        const response = await fetch(`/api/farms/${farmId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert("Ферму успішно видалено");
            currentUser.farms = currentUser.farms.filter(f => f.id !== farmId);
            renderFarmsList();
        } else {
            alert("Помилка видалення на сервері");
        }
    } catch (err) {
        console.error("Помилка при видаленні:", err);
        alert("Сервер не відповідає");
    }
}

function updateProgressUI() {
    if (!allItems.length) return;
    const totalItems = allItems.length;
    const totalCaught = caughtIds.length;
    const totalPercent = Math.round((totalCaught / totalItems) * 100) || 0;

    const totalFill = document.getElementById("total-progress-fill");
    const totalPercentText = document.getElementById("total-percent");
    const totalCountText = document.getElementById("total-count-text");

    if (totalFill) totalFill.style.width = totalPercent + "%";
    if (totalPercentText) totalPercentText.innerText = totalPercent + "%";
    if (totalCountText) totalCountText.innerText = `${totalCaught} / ${totalItems}`;

    const categories = ["Риба", "Відправка", "Артефакти", "Мінерали", "Їжа"];

    categories.forEach(cat => {
        const catItems = allItems.filter(item => {
            let name = typeof item.category === 'object' ? item.category.name : item.category;
            return name === cat;
        });

        const catCaught = catItems.filter(item => caughtIds.includes(item.id)).length;
        const catTotal = catItems.length;

        const btn = document.querySelector(`.tab-btn[data-category="${cat}"] .tab-count`);
        if (btn) {
            btn.innerText = `(${catCaught}/${catTotal})`;
        }
    });
}

function showAchievementPopup(text) {
    const container = document.getElementById('toast-container');

    const toast = document.createElement('div');
    toast.className = 'achievement-toast';
    toast.innerHTML = `🏆 <span>${text}</span>`;

    container.appendChild(toast);

    setTimeout(() => toast.classList.add('show'), 100);

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 400);
    }, 5000);
}