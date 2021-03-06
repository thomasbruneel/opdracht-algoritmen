package be.kul.gantry.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Problem2 {

    private final int minX, maxX, minY, maxY;
    private final int maxLevels;
    private final List<Item> items;
    private final List<Job> inputJobSequence;
    private final List<Job> outputJobSequence;

    private final List<Gantry> gantries;
    private final List<Slot> slots;
    private final double safetyDistance;
    private final double pickupPlaceDuration;

    private HashMap<Integer,Slot> itemToSlot;

    private double time;

    public Problem2(int minX, int maxX, int minY, int maxY, int maxLevels,
                   List<Item> items, List<Gantry> gantries, List<Slot> slots,
                   List<Job> inputJobSequence, List<Job> outputJobSequence, int gantrySafetyDist, int pickupPlaceDuration,HashMap <Integer, Slot> itemToSlot) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.maxLevels = maxLevels;
        this.items = new ArrayList<>(items);
        this.gantries = new ArrayList<>(gantries);
        this.slots = new ArrayList<>(slots);
        this.inputJobSequence = new ArrayList<>(inputJobSequence);
        this.outputJobSequence = new ArrayList<>(outputJobSequence);
        this.safetyDistance = gantrySafetyDist;
        this.pickupPlaceDuration = pickupPlaceDuration;
        this.itemToSlot=itemToSlot;
        this.time=0;

    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }


    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxLevels() {
        return maxLevels;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Job> getInputJobSequence() {
        return inputJobSequence;
    }

    public List<Job> getOutputJobSequence() {
        return outputJobSequence;
    }

    public List<Gantry> getGantries() {
        return gantries;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public double getSafetyDistance() {
        return safetyDistance;
    }

    public double getPickupPlaceDuration() {
        return pickupPlaceDuration;
    }


    public HashMap<Integer, Slot> getItemToSlot() {
		return itemToSlot;
	}

	public void setItemToSlot(HashMap<Integer, Slot> itemToSlot) {
		this.itemToSlot = itemToSlot;
	}

	public void writeJsonFile(File file) throws IOException {
        JSONObject root = new JSONObject();

        JSONObject parameters = new JSONObject();
        root.put("parameters",parameters);

        parameters.put("gantrySafetyDistance",safetyDistance);
        parameters.put("maxLevels",maxLevels);
        parameters.put("pickupPlaceDuration",pickupPlaceDuration);

        JSONArray items = new JSONArray();
        root.put("items",items);

        for(Item item : this.items) {
            JSONObject jo = new JSONObject();
            jo.put("id",item.getId());

            items.add(jo);
        }


        JSONArray slots = new JSONArray();
        root.put("slots",slots);
        for(Slot slot : this.slots) {
            JSONObject jo = new JSONObject();
            jo.put("id",slot.getId());
            jo.put("cx",slot.getCenterX());
            jo.put("cy",slot.getCenterY());
            jo.put("minX",slot.getXMin());
            jo.put("maxX",slot.getXMax());
            jo.put("minY",slot.getYMin());
            jo.put("maxY",slot.getYMax());
            jo.put("z",slot.getZ());
            jo.put("type",slot.getType().name());
            jo.put("itemId",slot.getItem() == null ? null : slot.getItem().getId());

            slots.add(jo);
        }

        JSONArray gantries = new JSONArray();
        root.put("gantries",gantries);
        for(Gantry gantry : this.gantries) {
            JSONObject jo = new JSONObject();

            jo.put("id",gantry.getId());
            jo.put("xMin",gantry.getXMin());
            jo.put("xMax",gantry.getXMax());
            jo.put("startX",gantry.getStartX());
            jo.put("startY",gantry.getStartY());
            jo.put("xSpeed",gantry.getXSpeed());
            jo.put("ySpeed",gantry.getYSpeed());

            gantries.add(jo);
        }

        JSONArray inputSequence = new JSONArray();
        root.put("inputSequence",inputSequence);

        for(Job inputJ : this.inputJobSequence) {
            JSONObject jo = new JSONObject();
            jo.put("itemId",inputJ.getItem().getId());
            jo.put("fromId",inputJ.getPickup().getSlot().getId());

            inputSequence.add(jo);
        }

        JSONArray outputSequence = new JSONArray();
        root.put("outputSequence",outputSequence);

        for(Job outputJ : this.outputJobSequence) {
            JSONObject jo = new JSONObject();
            jo.put("itemId",outputJ.getItem().getId());
            jo.put("toId",outputJ.getPlace().getSlot().getId());

            outputSequence.add(jo);
        }

        try(FileWriter fw = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            fw.write(gson.toJson(root));
        }

    }

    public static Problem2 fromJson(File file) throws IOException, ParseException {


        JSONParser parser = new JSONParser();

        try(FileReader reader = new FileReader(file)) {
            JSONObject root = (JSONObject) parser.parse(reader);

            List<Item> itemList = new ArrayList<>();
            List<Slot> slotList = new ArrayList<>();
            List<Gantry> gantryList = new ArrayList<>();
            List<Job> inputJobList = new ArrayList<>();
            List<Job> outputJobList = new ArrayList<>();

            JSONObject parameters = (JSONObject) root.get("parameters");
            int safetyDist = ((Long)parameters.get("gantrySafetyDistance")).intValue();
            int maxLevels = ((Long)parameters.get("maxLevels")).intValue();
            int pickupPlaceDuration = ((Long)parameters.get("pickupPlaceDuration")).intValue();

            JSONArray items = (JSONArray) root.get("items");
            for(Object o : items) {
                int id = ((Long)((JSONObject)o).get("id")).intValue();

                Item c = new Item(id);
                itemList.add(c);
            }


            int overallMinX = Integer.MAX_VALUE, overallMaxX = Integer.MIN_VALUE;
            int overallMinY = Integer.MAX_VALUE, overallMaxY = Integer.MIN_VALUE;

            JSONArray slots = (JSONArray) root.get("slots");

            HashMap<Integer,Slot>itemToSlot=new HashMap<>();

            for(Object o : slots) {
                JSONObject slot = (JSONObject) o;

                int id = ((Long)slot.get("id")).intValue();
                int cx = ((Long)slot.get("cx")).intValue();
                int cy = ((Long)slot.get("cy")).intValue();
                int minX = ((Long)slot.get("minX")).intValue();
                int minY = ((Long)slot.get("minY")).intValue();
                int maxX = ((Long)slot.get("maxX")).intValue();
                int maxY = ((Long)slot.get("maxY")).intValue();
                int z = ((Long)slot.get("z")).intValue();

                overallMinX = Math.min(overallMinX,minX);
                overallMaxX = Math.max(overallMaxX,maxX);
                overallMinY = Math.min(overallMinY,minY);
                overallMaxY = Math.max(overallMaxY,maxY);

                Slot.SlotType type = Slot.SlotType.valueOf((String)slot.get("type"));
                Integer itemId = slot.get("itemId") == null ? null : ((Long)slot.get("itemId")).intValue();
                Item c = itemId == null ? null : itemList.get(itemId);

                Slot s = new Slot(id,cx,cy,minX,maxX,minY,maxY,z,type,c);
                slotList.add(s);

                //itemID toekennen aan slot als slot een item bevat
                if(c!=null){
                	itemToSlot.put(c.getId(),s);
                }
            }


            JSONArray gantries = (JSONArray) root.get("gantries");
            for(Object o : gantries) {
                JSONObject gantry = (JSONObject) o;


                int id = ((Long)gantry.get("id")).intValue();
                int xMin = ((Long)gantry.get("xMin")).intValue();
                int xMax = ((Long)gantry.get("xMax")).intValue();
                int startX = ((Long)gantry.get("startX")).intValue();
                int startY = ((Long)gantry.get("startY")).intValue();
                double xSpeed = ((Double)gantry.get("xSpeed")).doubleValue();
                double ySpeed = ((Double)gantry.get("ySpeed")).doubleValue();

                Gantry g = new Gantry(id, xMin, xMax, startX, startY, xSpeed, ySpeed);
                gantryList.add(g);
            }

            JSONArray inputJobs = (JSONArray) root.get("inputSequence");
            int jid = 0;
            for(Object o : inputJobs) {
                JSONObject inputJob = (JSONObject) o;

                int iid = ((Long) inputJob.get("itemId")).intValue();
                int sid = ((Long) inputJob.get("fromId")).intValue();

                Job job = new Job(jid++,itemList.get(iid),slotList.get(sid),null);
                inputJobList.add(job);
            }

            JSONArray outputJobs = (JSONArray) root.get("outputSequence");
            for(Object o : outputJobs) {
                JSONObject outputJob = (JSONObject) o;

                int iid = ((Long) outputJob.get("itemId")).intValue();
                int sid = ((Long) outputJob.get("toId")).intValue();

                Job job = new Job(jid++,itemList.get(iid),null, slotList.get(sid));
                outputJobList.add(job);
            }


            return new Problem2(
                    overallMinX,
                    overallMaxX,
                    overallMinY,
                    overallMaxY,
                    maxLevels,
                    itemList,
                    gantryList,
                    slotList,
                    inputJobList,
                    outputJobList,
                    safetyDist,
                    pickupPlaceDuration,
                    itemToSlot);
        }
    }



    public ArrayList<Move> solve() {

        ArrayList<Move> solution = new ArrayList<>();

        Slot inputslot = new Slot(-1,-1,-1,-1,-1,-1,-1,-1,Slot.SlotType.STORAGE,null),outputslot = new Slot(-1,-1,-1,-1,-1,-1,-1,-1,Slot.SlotType.STORAGE,null);
        Gantry inputGantry=gantries.get(0);
        Gantry outputGantry=gantries.get(1);


        Map<Integer,SlotTree> rows = new HashMap<>();
        for (Slot s : slots){
            if (s.getType() != Slot.SlotType.STORAGE){
                if (s.getType() == Slot.SlotType.INPUT) inputslot = s;
                else outputslot = s;
            } else {
                if (!rows.containsKey(s.getCenterY())) rows.put(s.getCenterY(), new SlotTree());
                rows.get(s.getCenterY()).addNode(s);
            }
        }
        for (Integer i : rows.keySet()) rows.get(i).constructTree();

        Iterator<Integer> it = rows.keySet().iterator();


        Iterator<Job> inputjobIT = inputJobSequence.iterator();


        int inputIndex=0;

        //assume: !outputjobList.isEmpty()
        Job outputJob = outputJobSequence.get(0);
        Item outputItem = outputJob.getItem();
        Slot slot = itemToSlot.get(outputItem.getId());
        ArrayList<Slot> overlappingSlots = new ArrayList<>();

        Slot buried_slot = null;
        //voldoende voorwaarde?
        while (!outputJobSequence.isEmpty() || inputjobIT.hasNext()) {
            //staat bepalen

            if (!overlappingSlots.isEmpty()) {
                //STAAT: Uitgraven
                while(!overlappingSlots.isEmpty()){
                  
                    Slot slot_blocking = overlappingSlots.remove(0);
                    //Gantry selecteren en verplaatsen naar eerste inputslot
                    if(inputGantry.getTime() <= outputGantry.getTime()){
                        moveIN(inputGantry,outputGantry,slot_blocking);
                        inputGantry.moveTo(slot_blocking);
                        inputGantry.pickup(slot_blocking.getItem(), pickupPlaceDuration);
                        itemToSlot.remove(slot_blocking.getItem().getId());
                        slot_blocking.setItem(null);
                        //Bestemming nieuw item bepalen
                        Slot leegSlot = new Slot(-1,-1,slot_blocking.getCenterY(),-1,-1,-1,-1,-1,null,null);
                        while(leegSlot.getCenterY()==slot_blocking.getCenterY()) {
                            if (!it.hasNext()) it = rows.keySet().iterator();
                            leegSlot = rows.get(it.next()).getEmptySlot();
                        }
                        moveIN(inputGantry,outputGantry,leegSlot);
                        inputGantry.moveTo(leegSlot);

                        leegSlot.setItem(inputGantry.getItemInCrane());
                        itemToSlot.put(inputGantry.getItemInCrane().getId(),leegSlot);
                        inputGantry.drop(pickupPlaceDuration);
                    } else {
                        moveOUT(outputGantry,inputGantry,slot_blocking);
                        outputGantry.moveTo(slot_blocking);
                        outputGantry.pickup(slot_blocking.getItem(), pickupPlaceDuration);
                        itemToSlot.remove(slot_blocking.getItem().getId());
                        slot_blocking.setItem(null);
                        //Bestemming nieuw item bepalen
                        Slot leegSlot = new Slot(-1,-1,slot_blocking.getCenterY(),-1,-1,-1,-1,-1,null,null);
                        while(leegSlot.getCenterY()==slot_blocking.getCenterY()) {
                            if (!it.hasNext()) it = rows.keySet().iterator();
                            leegSlot = rows.get(it.next()).getEmptySlot();
                        }
                        moveOUT(outputGantry,inputGantry,leegSlot);
                        outputGantry.moveTo(leegSlot);

                        leegSlot.setItem(outputGantry.getItemInCrane());
                        itemToSlot.put(outputGantry.getItemInCrane().getId(),leegSlot);
                        outputGantry.drop(pickupPlaceDuration);
                    }
                }
                moveOUT(outputGantry,inputGantry,buried_slot);
                outputGantry.moveTo(buried_slot);
                outputGantry.pickup(buried_slot.getItem(), pickupPlaceDuration);
                itemToSlot.remove(buried_slot.getItem().getId());
                buried_slot.setItem(null);
                outputGantry.moveTo(outputslot);

                outputGantry.drop(pickupPlaceDuration);

                outputJobSequence.remove(outputJob);
                outputJob = null;
                outputItem = null;
                buried_slot = null;

            } else {
                if (!outputJobSequence.isEmpty() && slot != null) {
                    if (inputjobIT.hasNext()) {
                        //STAAT: SIMULTAAN IN EN OUT
                        //while(!overlappingSlots.isEmpty() && outputjobIT.hasNext() && inputjobIT.hasNext() && geenOnbestaandItem)
                        //TODO: inputs en outputs simultaan verwerken
                    	Gantry gantry =getLatestGantry(inputGantry, outputGantry);
                    	if(gantry.getId()==inputGantry.getId()){
                    		
                    		Job job=inputjobIT.next();
                        	Item inputItem=job.getItem();

                        	inputGantry.moveTo(inputslot);
                        	inputGantry.pickup(inputItem, pickupPlaceDuration);

                            if (!it.hasNext()) it = rows.keySet().iterator();
                            Slot leegSlot = rows.get(it.next()).getEmptySlot();

                            moveIN(inputGantry,outputGantry,leegSlot);
                        	inputGantry.moveTo(leegSlot);

                            leegSlot.setItem(inputGantry.getItemInCrane());
                            itemToSlot.put(inputGantry.getItemInCrane().getId(),leegSlot);
                        	inputGantry.drop(pickupPlaceDuration);
                        	
                            leegSlot.setItem(inputItem);
                            itemToSlot.put(inputItem.getId(),leegSlot);

                            //System.out.println("IN/OUT: IN");

                        }
                    	
                    	else{
                    	    if(outputItem == null) {
                                outputJob = outputJobSequence.get(0);
                                outputItem = outputJob.getItem();
                            }
                            slot=itemToSlot.get(outputItem.getId());
                            if(slot!=null){			//als slot leeg is, eerst inputjobs doen
                                overlappingSlots = rows.get(slot.getCenterY()).findOverlapping(slot.getXMin(), slot.getXMax(), slot.getZ());
                                if(overlappingSlots.isEmpty()){
                               		moveOUT(outputGantry,inputGantry,slot);
                               		outputGantry.moveTo(slot);
                                   	outputGantry.pickup(outputItem, pickupPlaceDuration);
                                   	itemToSlot.remove(outputItem.getId());
                                   	slot.setItem(null);
                                   	outputGantry.moveTo(outputslot);

                                   	outputGantry.drop(pickupPlaceDuration);

                                   	outputJobSequence.remove(outputJob);
                                   	outputJob=null;
                                   	outputItem=null;

                                    //System.out.println("IN/OUT: OUT");
                                } else {
                                    buried_slot = slot;
                                }
                                	
                           	}

                    	}
                    } else {
                        while(overlappingSlots.isEmpty() && !outputJobSequence.isEmpty()) {
                            //TODO: enkel output -> move priorityOUT

                            if(outputItem == null) {
                                outputJob = outputJobSequence.get(0);
                                outputItem = outputJob.getItem();
                            } else {
                                outputItem = outputJob.getItem();
                                slot = itemToSlot.get(outputItem.getId());
                                if (slot != null) {            //als slot leeg is, eerst inputjobs doen
                                    overlappingSlots = rows.get(slot.getCenterY()).findOverlapping(slot.getXMin(), slot.getXMax(), slot.getZ());
                                    if (overlappingSlots.isEmpty()) {

                                        if(inputGantry.getxPosition() > slot.getCenterX()-(int) safetyDistance || inputGantry.getTime() < outputGantry.getTime()){
                                            //inputGantry verplaatsen
                                            Slot dummy = new Slot(-666,slot.getCenterX()-(int)safetyDistance,slot.getCenterY(),0,0,0,0,0,null,null);
                                            moveIN(inputGantry,outputGantry, dummy);
                                            inputGantry.moveTo(dummy);
                                        }
                                        moveOUT(outputGantry,inputGantry,slot);
                                        outputGantry.moveTo(slot);
                                        outputGantry.pickup(outputItem, pickupPlaceDuration);
                                        itemToSlot.remove(outputItem.getId());
                                        slot.setItem(null);
                                        if(inputGantry.getTime()<outputGantry.getTime()){
                                            inputGantry.stayIdle(outputGantry.getTime());
                                        }
                                        outputGantry.moveTo(outputslot);
                                        outputGantry.drop(pickupPlaceDuration);

                                        outputJobSequence.remove(outputJob);
                                        outputJob = null;
                                        outputItem = null;
                                    } else {
                                        buried_slot = slot;
                                    }

                                }

                            }
                            //System.out.println("OUT");
                        }
                    }
                } else {
                    //while(inputjobIT.hasNext() && !begravenItemGevonden)
                    //TODO: enkel inputs -> move_priorityIN
                	if(inputjobIT.hasNext()){
                		Job job=inputjobIT.next();
                    	Item inputItem=job.getItem();

                    	inputGantry.moveTo(inputslot);
                    	inputGantry.pickup(inputItem, pickupPlaceDuration);
                    	
                    	if(!it.hasNext()) it = rows.keySet().iterator();
                        Slot leegSlot = rows.get(it.next()).getEmptySlot();

                        if(outputGantry.getxPosition() < leegSlot.getCenterX()+(int) safetyDistance && inputGantry.getTime() > outputGantry.getTime()){
                            outputGantry.moveAway(leegSlot.getCenterX()+(int) safetyDistance);
                        } else {
                            moveIN(inputGantry,outputGantry,leegSlot);
                        }
                    	inputGantry.moveTo(leegSlot);       //TODO: move_priority_IN

                        leegSlot.setItem(inputGantry.getItemInCrane());
                        itemToSlot.put(inputGantry.getItemInCrane().getId(),leegSlot);
                    	inputGantry.drop(pickupPlaceDuration);

                    	outputGantry.stayIdle(inputGantry.getLastCranestate().getT());

                        leegSlot.setItem(inputItem);
                        itemToSlot.put(inputItem.getId(),leegSlot);
                        if(outputItem.getId()==inputItem.getId()){
                            slot = itemToSlot.get(outputItem.getId());
                        }
                	}


                    //System.out.println("IN");

                }
            }

        }
        inputGantry.moveTo(inputslot);
        outputGantry.moveTo(outputslot);
        ArrayList<Move>oplossing=merge(inputGantry,outputGantry);
        System.out.println("---------Opgelost----------");
        return oplossing;
    }

    //Opgeroepen wanneer in richting out beweegt => om iets op te pakken of af te zetten
    public void moveIN(Gantry inputGantry, Gantry outputGantry, Slot s){
        //System.out.println("moveIN");
        //relevante states van outputGantry opvragen
        List<CraneState> obstacles = outputGantry.getStates(inputGantry.getTime());
        CraneState lastState = inputGantry.getLastCranestate();
        List<CraneState> detour = new ArrayList<>();

        while(lastState.getT()<outputGantry.getTime()){
            //Attempt opstellen
            double attemptTime = Math.max(Math.abs(lastState.getX()-s.getCenterX())/inputGantry.getXSpeed(),Math.abs(lastState.getY()-s.getCenterY())/inputGantry.getYSpeed());
            CraneState attempt = new CraneState(s.getCenterX(),s.getCenterY(),lastState.getT()+attemptTime,inputGantry.getItemInCrane());

            //intersection met attemptlijn controleren + extra pickup of droptijd in rekening brengen
            boolean intersection = false;
            Line2D attemptLine = new Line2D.Double(new Point2D.Double(lastState.getX(),lastState.getT()),new Point2D.Double(attempt.getX(),attempt.getT()));
            Line2D pickDropLine = new Line2D.Double(new Point2D.Double(attempt.getX(),attempt.getT()),new Point2D.Double(attempt.getX(),attempt.getT()+pickupPlaceDuration));

            CraneState crash = null;
            for (int i=0; i<obstacles.size()-1 && !intersection; i++){   //SAFETYDISTANCE!!!
                Line2D obstacleLine = new Line2D.Double(new Point2D.Double(obstacles.get(i).getX()-safetyDistance,obstacles.get(i).getT()),new Point2D.Double(obstacles.get(i+1).getX()-safetyDistance,obstacles.get(i+1).getT()));
                if((attemptLine.intersectsLine(obstacleLine) || pickDropLine.intersectsLine(obstacleLine)) && obstacles.get(i+1).getT()>lastState.getT()) {
                    intersection = true;
                    crash = obstacles.get(i+1);
                }
            }

            if(intersection){

                double detourTime = Math.max(Math.abs(lastState.getX()-(crash.getX()-safetyDistance))/inputGantry.getxSpeed(),-666);

                lastState = new CraneState(crash.getX()-(int)safetyDistance,crash.getY(),Math.max(lastState.getT()+detourTime,crash.getT()),inputGantry.getItemInCrane());


                //kan mss in 1 move?
                /*
                detour.add(new CraneState(crash.getX() - (int)safetyDistance,lastState.getY(),lastState.getT()+detourTime,inputGantry.getItemInCrane()));
                if(!(crash.getT()<lastState.getT()+detourTime)) {
                    lastState = new CraneState(crash.getX() - (int) safetyDistance, lastState.getY(), crash.getT(), inputGantry.getItemInCrane());
                } else lastState = detour.get(detour.size()-1);
                */
                detour.add(lastState);
            } else {
                inputGantry.getStates().addAll(detour);
                inputGantry.setxPosition(lastState.getX());
                inputGantry.setyPostion(lastState.getY());
                inputGantry.setTime(lastState.getT());
                inputGantry.setItemInCrane(lastState.getItem());
                return;
            }

        }
        inputGantry.getStates().addAll(detour);
        inputGantry.setxPosition(lastState.getX());
        inputGantry.setyPostion(lastState.getY());
        inputGantry.setTime(lastState.getT());
        inputGantry.setItemInCrane(lastState.getItem());

    }

    public void moveOUT(Gantry outputGantry, Gantry inputGantry, Slot s){
        //System.out.println("moveOUT");
        //relevante states inputgantry opvragen
        List<CraneState> obstacles = inputGantry.getStates(outputGantry.getTime());
        CraneState lastState = outputGantry.getLastCranestate();
        List<CraneState> detour = new ArrayList<>();

        while(lastState.getT()<inputGantry.getTime()){
            //Attempt opstellen
            double attemptTime = Math.max(Math.abs(lastState.getX()-s.getCenterX())/outputGantry.getXSpeed(),Math.abs(lastState.getY()-s.getCenterY())/outputGantry.getYSpeed());
            CraneState attempt = new CraneState(s.getCenterX(),s.getCenterY(),lastState.getT()+attemptTime,outputGantry.getItemInCrane());

            //intersection met attemptlijn controleren + extra pickup of droptijd in rekening brengen
            boolean intersection = false;
            Line2D attemptLine = new Line2D.Double(new Point2D.Double(lastState.getX(),lastState.getT()),new Point2D.Double(attempt.getX(),attempt.getT()));
            Line2D pickDropLine = new Line2D.Double(new Point2D.Double(attempt.getX(),attempt.getT()),new Point2D.Double(attempt.getX(),attempt.getT()+pickupPlaceDuration));

            CraneState crash = null;
            for (int i=0; i<obstacles.size()-1 && !intersection; i++){   //SAFETYDISTANCE!!!
                Line2D obstacleLine = new Line2D.Double(new Point2D.Double(obstacles.get(i).getX()+safetyDistance,obstacles.get(i).getT()),new Point2D.Double(obstacles.get(i+1).getX()+safetyDistance,obstacles.get(i+1).getT()));
                if((attemptLine.intersectsLine(obstacleLine) || pickDropLine.intersectsLine(obstacleLine)) && obstacles.get(i+1).getT()>lastState.getT()) {
                    intersection = true;
                    crash = obstacles.get(i+1);
                }
            }

            if(intersection){

                double detourTime = Math.max(Math.abs(lastState.getX()-(crash.getX()+safetyDistance))/outputGantry.getxSpeed(),-666);

                lastState = new CraneState(crash.getX()+(int)safetyDistance,crash.getY(),Math.max(lastState.getT()+detourTime,crash.getT()),outputGantry.getItemInCrane());

                //kan mss in 1 move?
                /*
                detour.add(new CraneState(crash.getX()+(int)safetyDistance,crash.getY(),lastState.getT()+detourTime,outputGantry.getItemInCrane()));
                if(!(crash.getT()<lastState.getT()+detourTime)) {
                    lastState = new CraneState(crash.getX() + (int) safetyDistance, lastState.getY(), crash.getT(), outputGantry.getItemInCrane());
                } else lastState = detour.get(detour.size()-1);
                */
                detour.add(lastState);
            } else {
                outputGantry.getStates().addAll(detour);
                outputGantry.setxPosition(lastState.getX());
                outputGantry.setyPostion(lastState.getY());
                outputGantry.setTime(lastState.getT());
                outputGantry.setItemInCrane(lastState.getItem());
                return;
            }

        }
        outputGantry.getStates().addAll(detour);
        outputGantry.setxPosition(lastState.getX());
        outputGantry.setyPostion(lastState.getY());
        outputGantry.setTime(lastState.getT());
        outputGantry.setItemInCrane(lastState.getItem());

    }

    private Gantry getLatestGantry(Gantry inputGantry, Gantry outputGantry) {
        return inputGantry.getTime()<outputGantry.getTime() ? inputGantry : outputGantry;
    }
    private ArrayList<Move> merge(Gantry inputGantry, Gantry outputGantry) {
    	ArrayList<CraneState>inputStates=inputGantry.getStates();
    	ArrayList<CraneState>outputStates=outputGantry.getStates();

		ArrayList<Move>moves = new ArrayList<>();
		
		for(CraneState craneStateInput:inputStates){
			moves.add(new Move(inputGantry,craneStateInput));
		}
		for(CraneState CraneStateOutput:outputStates){
			moves.add(new Move(outputGantry,CraneStateOutput));
		}
		
		Collections.sort(moves, new Comparator<Move>(){
			public int compare(Move m1, Move m2){
				return (int) (m1.getT()-m2.getT());
			}
		});

		return moves;
	}

    private void updateCurrentTime() {
        double lowestTime = Integer.MAX_VALUE;
        for (Gantry g: gantries){
            if(g.isWorking() && g.getTime()<lowestTime) lowestTime=g.getTime();
        }
        time = lowestTime;
    }

    private boolean collision(Gantry van, Gantry tussen, Slot naar, double safetyDistance) {
        if(van.getId() == 0){
            return tussen.getxPosition() < naar.getCenterX()+safetyDistance;
        } else return tussen.getxPosition() > naar.getCenterX()-safetyDistance;
    }

    private void removeOverlappingSlots(Slot slot, Map<Integer, SlotTree> rows, ArrayList<Move> solution, Iterator<Integer> it, List<Gantry> gantries, HashMap<Integer, Slot> itemToSlot, int pickupPlaceDuration) {

        //Alle bovenliggende slots bepalen
        ArrayList<Slot> overlappingSlots = rows.get(slot.getCenterY()).findOverlapping(slot.getXMin(), slot.getXMax(), slot.getZ());
        //System.out.println(overlappingSlots);
        for(Slot s:overlappingSlots){
            //removeOverlappingSlots(s,rows,solution,it,gantries,itemToSlot,pickupPlaceDuration);

            //Dichtste kraan selecteren
            int gantryindex = getClosestGantry(gantries,s);

            //collision
            if(gantryindex == 0){
                //Collision met outputkraan vermijden
                if(collision(gantries.get(0),gantries.get(1),s,safetyDistance)){
                    solution.add(new Move(gantries,1,s.getCenterX()+(int)safetyDistance,gantries.get(1).getyPostion(),0));
                }
            } else{
                //Collision met InputKraan vermijden
                if(collision(gantries.get(1),gantries.get(0),s,safetyDistance)){
                    solution.add(new Move(gantries,0,s.getCenterX()-(int)safetyDistance,gantries.get(0).getyPostion(),0));
                }
            }

            //Kraan verplaatsen naar te verplaatsen item
            solution.add(new Move(gantries,gantryindex,s.getCenterX(),s.getCenterY(),0));
            //item oppakken
            gantries.get(gantryindex).setItemInCrane(s.getItem());
            itemToSlot.remove(s.getItem().getId());
            s.setItem(null);
            solution.add(new Move(gantries,gantryindex,s.getCenterX(),s.getCenterY(),pickupPlaceDuration));
            //Leeg slot kiezen dat niet in dezelfde rij ligt om nieuwe overlapping te voorkomen
            Slot leegSlot;
            do {
                if(!it.hasNext()) it = rows.keySet().iterator();
                leegSlot = rows.get(it.next()).getEmptySlot();
            } while (leegSlot.getCenterY() == slot.getCenterY());

            //collision
            if(gantryindex == 0){
                //Collision met outputkraan vermijden
                if(collision(gantries.get(0),gantries.get(1),leegSlot,safetyDistance)){
                    solution.add(new Move(gantries,1,leegSlot.getCenterX()+(int)safetyDistance,gantries.get(1).getyPostion(),0));
                }
            } else{
                //Collision met inputkraan vermijden
                if(collision(gantries.get(1),gantries.get(0),leegSlot,safetyDistance)){
                    solution.add(new Move(gantries,0,leegSlot.getCenterX()-(int)safetyDistance,gantries.get(0).getyPostion(),0));
                }
            }

            //Kraan verplaatsen naar bestemming
            solution.add(new Move(gantries,gantryindex,leegSlot.getCenterX(),leegSlot.getCenterY(),0));
            //item neerleggen
            leegSlot.setItem(gantries.get(gantryindex).getItemInCrane());
            itemToSlot.put(gantries.get(gantryindex).getItemInCrane().getId(),leegSlot);
            gantries.get(gantryindex).setItemInCrane(null);
            solution.add(new Move(gantries,gantryindex,leegSlot.getCenterX(),leegSlot.getCenterY(),pickupPlaceDuration));
        }


    }

    private int getClosestGantry(List<Gantry> gantries, Slot s) {
        int index = Integer.MIN_VALUE;
        int closestDistenceToSlot = Integer.MAX_VALUE;
        for (Gantry g: gantries) {
            if(Math.abs(g.getxPosition()-s.getCenterX()) < closestDistenceToSlot){
                closestDistenceToSlot = Math.abs(g.getxPosition()-s.getCenterX());
                index = g.getId();
            }
        }
        return index;
    }

    @Override
    public String toString() {
        return "\nminX: " + minX
                + "\nmaxX: " + maxX
                + "\nminY: " + minY
                + "\nmaxY: " + maxY
                + "\nmaxLevels: " + maxLevels
                + "\nsafetyDistance: " + safetyDistance;
    }
}
