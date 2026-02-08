using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.EntityFrameworkCore;
using RWAEvent.BL.Mapper;
using RWAEvent.BL.Models;
using RWAEvent.BL.Services;

var supportedCultures = new[] { "en", "hr" };
var localizationOptions = new RequestLocalizationOptions()
    .SetDefaultCulture("en")
    .AddSupportedCultures(supportedCultures)
    .AddSupportedUICultures(supportedCultures);

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddLocalization(options => options.ResourcesPath = "Resources");

builder.Services.AddControllersWithViews()
    .AddViewLocalization()
    .AddDataAnnotationsLocalization();

builder.Services.AddDbContext<RwaeventContext>(opts =>
{
    opts.UseSqlServer(builder.Configuration.GetConnectionString("RWAEvent"));
});

builder.Services.AddScoped<IEvent, EventService>();
builder.Services.AddScoped<IEventType, EventTypeService>();
builder.Services.AddScoped<IPerformer, PerformerService>();
builder.Services.AddScoped<IReservation, ReservationService>();
builder.Services.AddScoped<IUser, UserService>();
builder.Services.AddScoped<IEventLogger, LoggerService>();

builder.Services.AddDistributedMemoryCache();

builder.Services.AddAutoMapper(cfg =>
{
    cfg.AddMaps(typeof(EventProfile).Assembly);
});

builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromHours(1);
    options.Cookie.HttpOnly = true;
    options.Cookie.IsEssential = true;
});

builder.Services.AddAuthentication(options =>
{
    options.DefaultScheme = CookieAuthenticationDefaults.AuthenticationScheme;
})
.AddCookie(options =>
{
    options.LoginPath = "/Login/Login";
    options.LogoutPath = "/Login/Logout";
    options.AccessDeniedPath = "/Login/Forbidden";
    options.SlidingExpiration = true;
    options.ExpireTimeSpan = TimeSpan.FromMinutes(20);
});

var app = builder.Build();

app.UseExceptionHandler("/Error/Error");

app.UseStaticFiles();

app.UseRequestLocalization(localizationOptions);

app.UseRouting();
app.UseSession();

app.UseAuthentication();
app.UseAuthorization();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Login}/{action=About}/{id?}");

app.MapControllers();

app.Run();