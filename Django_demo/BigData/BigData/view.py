from django.shortcuts import render

def homePage(request):
    context = {}
    context['hello'] = 'This is Home Page'
    return render(request, 'hello.html', context)