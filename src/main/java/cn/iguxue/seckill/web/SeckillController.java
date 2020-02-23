package cn.iguxue.seckill.web;

import cn.iguxue.seckill.dto.Exposer;
import cn.iguxue.seckill.dto.SeckillExecution;
import cn.iguxue.seckill.dto.SeckillResult;
import cn.iguxue.seckill.entity.Seckill;
import cn.iguxue.seckill.enums.SeckillStateEnum;
import cn.iguxue.seckill.exception.RepeatKillException;
import cn.iguxue.seckill.exception.SeckillCloseException;
import cn.iguxue.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SecKillService secKillService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Seckill> seckills = secKillService.getSeckillList();
        model.addAttribute("list",seckills);
        return "/seckill";
    }

    @GetMapping("/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }

        Seckill seckill = secKillService.getById(seckillId);
        if (seckill == null) {
            return "redirect:/seckill/list";
        }

        model.addAttribute("seckill",seckill);
        return "seckill_detail";
    }

    @PostMapping("/{seckillId}/exposer")
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = secKillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<>(true, exposer);
        } catch (Exception e) {
            result = new SeckillResult<>(false, e.getMessage());
        }
        return result;
    }

    @PostMapping("/{seckillId}/{md5}/execution")
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long phone) {
        if (phone == null) {
            System.out.println("电话为空");
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }

        SeckillResult<SeckillExecution> result;
        try {
            SeckillExecution execution = secKillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (SeckillCloseException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (Exception e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, execution);
        }
    }

    @GetMapping("/time/now")
    @ResponseBody
    public SeckillResult<Long> time() {
        Date nowTime = new Date();
        return new SeckillResult(true, nowTime.getTime());
    }

}
