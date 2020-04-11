
function removeActiveClass(node) 
{
        node.className = '';
}

function active_on() 
{
    document.querySelector('ul[id=nav]').onclick = function (e) 
    {
        Array.prototype.forEach.call(document.querySelectorAll('ul[id=nav] > li'), removeActiveClass);
        var target = e.target;
        target.parentElement.className = 'active';
    }
}