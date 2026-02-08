using RWAEvent.WebApp.ViewsModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Models.Filters;
using RWAEvent.BL.Services;
using RWAEvent.BL.Models.DTO.Performer;

namespace RWAEvent.WebApp.Controllers
{
    public class PerformerController : Controller
    {
        private readonly IPerformer _performerService;

        public PerformerController(IPerformer performerService)
        {
            _performerService = performerService;
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> List(string? search, int page = 1, int pageSize = 10)
        {
            var filter = new PerformerFilter
            {
                Search = search,
                Page = page,
                PageSize = pageSize
            };

            var result = await _performerService.GetPerformersByFilter(filter);

            var model = new PerformerVM
            {
                Performers = result.Performers,
                CurrentPage = page,
                TotalPages = (int)Math.Ceiling((double)result.TotalCount / pageSize),
                Filter = filter
            };

            return View(model);
        }

        [HttpPost]
        [Authorize]
        public IActionResult List(PerformerVM viewModel)
        {
            var filter = viewModel.Filter ?? new PerformerFilter();

            return RedirectToAction("List", new
            {
                page = 1,
                pageSize = filter.PageSize,
                search = filter.Search
            });
        }

        [HttpGet]
        [Authorize(Roles = "Admin")]
        public IActionResult Create()
        {
            return View();
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Create(PerformerDTO dto)
        {
            if (!ModelState.IsValid)
            {
                return View(dto);
            }

            await _performerService.CreatePerformer(dto);
            return RedirectToAction("List");
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Delete(int id)
        {
            bool isDeleted = await _performerService.DeletePerformer(id);
            if (!isDeleted)
            {
                return NotFound();
            }

            return RedirectToAction("List");
        }

        [HttpGet]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Edit(int id)
        {
            var performer = await _performerService.GetPerformerById(id);
            return View(performer);
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Edit(PerformerDTO dto)
        {
            if (!ModelState.IsValid)
                return View(dto);

            bool updated = await _performerService.UpdatePerformer(dto);
            if (!updated)
            {
                return NotFound();

            }

            return RedirectToAction("List");
        }
    }
}
