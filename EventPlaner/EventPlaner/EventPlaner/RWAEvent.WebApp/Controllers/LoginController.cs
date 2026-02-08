using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Localization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Localization;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.Enums;
using RWAEvent.BL.Security;
using RWAEvent.WebApp.ViewsModel;
using System.Globalization;
using System.Resources;
using System.Security.Claims;

namespace RWAEvent.WebApp.Controllers
{
    public class LoginController : Controller
    {
        private readonly RwaeventContext _context;
        private readonly string _defaultLanguage;

        public LoginController(RwaeventContext context, IStringLocalizer<LoginController> localizer, IConfiguration configuration)
        {
            _context = context;
            _defaultLanguage = configuration["Language:Default"] ?? "hr";
        }

        [HttpGet]
        [AllowAnonymous]
        public IActionResult Login(string returnUrl)
        {
            var requestCulture = HttpContext.Features.Get<IRequestCultureFeature>();
            var currentCulture = requestCulture?.RequestCulture.Culture.Name ?? _defaultLanguage;

            SetLanguage(currentCulture);

            return View(new LoginVM
            {
                SelectedLanguage = currentCulture,
                ReturnUrl = returnUrl
            });
        }

        [HttpPost]
        [AllowAnonymous]
        public IActionResult Login(LoginVM loginVM)
        {
            SetLanguage(loginVM.SelectedLanguage);

            if (!ModelState.IsValid)
            {
                return View(loginVM);
            }

            var resourceManager = new ResourceManager("RWAEvent.WebApp.Resources.Views.Login.Login", typeof(LoginController).Assembly);
            var existingUser = _context.Users.FirstOrDefault(u => u.Username == loginVM.Username && u.Active);

            if (existingUser == null)
            {
                ModelState.AddModelError("", resourceManager.GetString("erUserDontExist", CultureInfo.CurrentUICulture));
                return View(loginVM);
            }

            var b64hash = PasswordHashProvider.GetHash(loginVM.Password, existingUser.PwdSalt);

            if (b64hash != existingUser.PwdHash)
            {
                ModelState.AddModelError("", resourceManager.GetString("erWrongPassword", CultureInfo.CurrentUICulture));
                return View(loginVM);
            }

            string role = existingUser.Role == 0 ? "User" : "Admin";

            var claims = new List<Claim>
                {
                    new Claim(ClaimTypes.Name, existingUser.Username),
                    new Claim(ClaimTypes.Role, role)
                };

            var claimsIdentity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
            var authProperties = new AuthenticationProperties();

            var task = Task.Run(() =>
            {
                HttpContext.SignInAsync(
                    CookieAuthenticationDefaults.AuthenticationScheme,
                    new ClaimsPrincipal(claimsIdentity),
                    authProperties);
            });
            task.GetAwaiter().GetResult();

            if (loginVM.ReturnUrl != null)
            {
                return LocalRedirect(loginVM.ReturnUrl);
            }

            return RedirectToAction("List", "Event");
        }

        [HttpGet]
        [Authorize]
        public IActionResult Logout()
        {
            Task.Run(async () =>
                    await HttpContext.SignOutAsync(
                        CookieAuthenticationDefaults.AuthenticationScheme)
                ).GetAwaiter().GetResult();

            return RedirectToAction("About");
        }

        [HttpGet]
        [AllowAnonymous]
        public IActionResult Registration()
        {
            var model = new RegistrationVM();
            return View(model);
        }

        [HttpPost]
        [AllowAnonymous]
        public IActionResult Registration(RegistrationVM registrationVM)
        {
            if (!ModelState.IsValid)
            {
                return View(registrationVM);
            }

            var trimmedUser = registrationVM.Username.Trim();
            var resourceManager = new ResourceManager("RWAEvent.WebApp.Resources.Views.Login.Registration", typeof(LoginController).Assembly);

            if (_context.Users.Any(usr => usr.Username.Equals(trimmedUser)))
            {
                ModelState.AddModelError("", resourceManager.GetString("erUserAlreadyExist", CultureInfo.CurrentUICulture));
                return View(registrationVM);
            }

            var b64salt = PasswordHashProvider.GetSalt();
            var b64hash = PasswordHashProvider.GetHash(registrationVM.Password, b64salt);

            var user = new User
            {
                Username = registrationVM.Username,
                PwdHash = b64hash,
                PwdSalt = b64salt,
                FirstName = registrationVM.FirstName,
                LastName = registrationVM.LastName,
                Email = registrationVM.Email,
                Phone = registrationVM.Phone,
                CreatedOn = DateTime.Now,
                Active = true
            };

            _context.Users.Add(user);
            _context.SaveChanges();
            return RedirectToAction("Login", "Login");
        }

        [HttpGet]
        [AllowAnonymous]
        public IActionResult ChangeLanguage(string culture)
        {
            if (!string.IsNullOrEmpty(culture))
            {
                Response.Cookies.Append(
                    CookieRequestCultureProvider.DefaultCookieName,
                    CookieRequestCultureProvider.MakeCookieValue(new RequestCulture(culture)),
                    new CookieOptions { Expires = DateTimeOffset.UtcNow.AddYears(1) }
                );
            }

            return RedirectToAction("Login");
        }

        [HttpGet]
        [AllowAnonymous]
        public async Task<IActionResult> About()
        {
            return View();
        }

        [HttpGet]
        [AllowAnonymous]
        private void SetLanguage(string selectedCulture)
        {
            ViewBag.Languages = Enum.GetNames(typeof(Language)).ToList();
            ViewBag.SelectedLanguage = selectedCulture;
        }
    }
}
