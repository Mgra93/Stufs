using Microsoft.EntityFrameworkCore;
using RWAEvent.BL.Models;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using RWAEvent.BL.Services;
using RWAEvent.BL.Mapper;
using System.Text;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddControllers();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(option =>
{
    option.SwaggerDoc("v1",
        new OpenApiInfo { Title = "RWA Event API", Version = "v1" });

    option.AddSecurityDefinition("Bearer",
        new OpenApiSecurityScheme
        {
            In = ParameterLocation.Header,
            Description = "Please enter valid JWT",
            Name = "Authorization",
            Type = SecuritySchemeType.Http,
            BearerFormat = "JWT",
            Scheme = "Bearer"
        });

    option.AddSecurityRequirement(
        new OpenApiSecurityRequirement
        {
            {
                new OpenApiSecurityScheme
                {
                    Reference = new OpenApiReference
                    {
                        Type = ReferenceType.SecurityScheme,
                        Id = "Bearer"
                    },
                },
                new List<string>()
            }
        });
});

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

builder.Services.AddAutoMapper(cfg =>
{
    cfg.AddMaps(typeof(EventProfile).Assembly);
});


var secureKey = builder.Configuration["Jwt:SecureKey"];

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(o =>
    {
        var Key = Encoding.UTF8.GetBytes(secureKey);
        o.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = false,
            ValidateAudience = false,
            IssuerSigningKey = new SymmetricSecurityKey(Key)
        };
    });

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseStaticFiles();

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();
