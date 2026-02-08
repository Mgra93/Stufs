using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace RWAEvent.WebApp.Controllers
{
    [AllowAnonymous]
    public class ErrorController : Controller
    {

        [HttpGet]
        public IActionResult Error()
        {
            return View();
        }
    }
}
